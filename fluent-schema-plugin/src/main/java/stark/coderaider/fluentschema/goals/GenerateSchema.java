package stark.coderaider.fluentschema.goals;

import lombok.SneakyThrows;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.util.CollectionUtils;
import stark.coderaider.fluentschema.codegen.SchemaMigrationCodeGenerator;
import stark.coderaider.fluentschema.codegen.SnapshotCodeGenerator;
import stark.coderaider.fluentschema.commons.NamingConverter;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.parsing.EntityParser;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Add @Execute(phase = LifecyclePhase.COMPILE) to make sure the project will be compiled before the execution of this goa.
// Because this goal relies on the compiled .class files.
@Mojo(name = "generate-schema", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME)
@Execute(phase = LifecyclePhase.COMPILE)
public class GenerateSchema extends AbstractMojo
{
    public static final String SCHEMA_MIGRATION = "SchemaMigration";
    public static final SimpleDateFormat DATE_FORMAT;
    public static final String DEFAULT_SCHEMA_NAME = "SchemaSnapshot";

    @Parameter(property = "entityPackage", required = true)
    private String entityPackage;

    @Parameter(property = "schemaPackage", required = true)
    private String schemaPackage;

    @Parameter(property = "dataSourceName")
    private String dataSourceName;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    private List<String> dependencies;
    private File sourceDirectory;
    private File outputDirectory;
    private String schemaSnapshotClassName;

    static
    {
        DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException
    {
        initializeSourceAndOutputDirectories();
        validateDirectoryParameters();
        resolveProjectDependencies();
        prepareSchemaPackage();

        List<Class<?>> entityClasses = loadEntityClasses();
        if (CollectionUtils.isEmpty(entityClasses))
        {
            getLog().info("No entity classes found.");
            return;
        }

        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();
        for (Class<?> entityClass : entityClasses)
        {
            EntityParser parser = new EntityParser();
            TableSchemaInfo tableSchemaInfo = parser.parse(entityClass);
            newTableSchemaInfos.add(tableSchemaInfo);
        }

        Class<?> schemaSnapshotClass = getSchemaSnapshotClass();
        boolean initialized;
        List<TableSchemaInfo> oldTableSchemaInfos;
        if (schemaSnapshotClass == null)
        {
            initialized = false;
            oldTableSchemaInfos = new ArrayList<>();
        }
        else
        {
            initialized = true;

            String schemaSnapshotClassName = schemaSnapshotClass.getName();
            try
            {
                Constructor<?> constructor = schemaSnapshotClass.getConstructor();
                Object schemaSnapshot = constructor.newInstance();
                Method mGetTableSchemaInfos = schemaSnapshotClass.getMethod("getTableSchemaInfos");
                oldTableSchemaInfos = (List<TableSchemaInfo>) mGetTableSchemaInfos.invoke(schemaSnapshot);
            }
            catch (NoSuchMethodException e)
            {
                throw new MojoExecutionException("There is no method \"public List<TableSchemaInfo> getTableSchemaInfos()\" in " + schemaSnapshotClassName, e);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new MojoExecutionException("Error instantiating " + schemaSnapshotClassName, e);
            }
        }

//        String schemaMigrationHistoryClassName = getSchemaMigrationHistoryClassName();
//        String codeOfSchemaMigration = SchemaMigrationCodeGenerator.generateSchemaMigration(schemaPackage, schemaMigrationHistoryClassName, newTableSchemaInfos, oldTableSchemaInfos, initialized);

        String schemaSnapshotClassSimpleName = schemaSnapshotClassName.substring(schemaSnapshotClassName.lastIndexOf('.') + 1);
        String codeOfSchemaSnapshot = SnapshotCodeGenerator.generateSchemaSnapshot(schemaPackage, schemaSnapshotClassSimpleName, newTableSchemaInfos);
        writeCodeToClass(schemaSnapshotClassSimpleName, codeOfSchemaSnapshot);
    }

    private void initializeSourceAndOutputDirectories()
    {
        String sourceDirectoryPath = session.getCurrentProject().getBuild().getSourceDirectory();
        sourceDirectory = new File(sourceDirectoryPath);

        String outputDirectoryPath = session.getCurrentProject().getBuild().getOutputDirectory();
        outputDirectory = new File(outputDirectoryPath);
    }



    private Class<?> getSchemaSnapshotClass() throws MojoExecutionException
    {
        schemaSnapshotClassName = getSchemaSnapshotClassName();
        getLog().info("Schema class name: " + schemaSnapshotClassName);

        List<Class<?>> schemaSnapshotClasses = loadSchemaSnapshotClasses();
        for (Class<?> schemaSnapshotClass : schemaSnapshotClasses)
        {
            if (schemaSnapshotClassName.equals(schemaSnapshotClass.getName()))
                return schemaSnapshotClass;
        }

        return null;
    }

    private String getSchemaSnapshotClassName()
    {
        getLog().info("Schema package: " + schemaPackage);
        String dataSourceClassName = NamingConverter.toClassLikeName(dataSourceName);
        return schemaPackage + "." + dataSourceClassName + DEFAULT_SCHEMA_NAME;
    }

    private void prepareSchemaPackage() throws MojoExecutionException
    {
        if (schemaPackage == null || schemaPackage.isEmpty())
            schemaPackage = entityPackage.substring(0, entityPackage.lastIndexOf('.')) + ".schemas";

        String schemaPackagePath = sourceDirectory.getAbsolutePath() + File.separator + schemaPackage.replace('.', File.separatorChar);
        File schemaPackageDir = new File(schemaPackagePath);

        // Create the directory of the schema package if it does not exist.
        if (!schemaPackageDir.exists())
        {
            if (!schemaPackageDir.mkdirs())
                throw new MojoExecutionException("Failed to create output directory: " + schemaPackageDir);
            getLog().info("Schema " + schemaPackage + " created.");
        }
    }

    private List<File> findClassesInPackage(String packageName)
    {
        List<File> javaFiles = new ArrayList<>();
        findClassesInPackage(sourceDirectory, packageName, javaFiles);
        return javaFiles;
    }

    private List<Class<?>> loadEntityClasses() throws MojoExecutionException
    {
        getLog().info("Analyzing sources in directory: " + entityPackage);

        List<File> javaFiles = findClassesInPackage(entityPackage);
        if (javaFiles.isEmpty())
        {
            getLog().warn("No Java files found in the specified package: " + entityPackage);
            throw new MojoExecutionException("No Java files found in the specified package: " + entityPackage);
        }

        try
        {
            return compileAndLoadClasses(javaFiles);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error during class loading or compilation", e);
        }
    }

    private List<Class<?>> loadSchemaSnapshotClasses() throws MojoExecutionException
    {
        getLog().info("Analyzing sources in directory: " + schemaPackage);

        List<File> javaFiles = findClassesInPackage(schemaPackage);
        if (javaFiles.isEmpty())
            return new ArrayList<>();

        getLog().info("javaFiles = " + String.join(";", javaFiles.stream().map(File::getAbsolutePath).toList()));

        try
        {
            return compileAndLoadClasses(javaFiles);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error during class loading or compilation", e);
        }
    }

    private List<Class<?>> compileAndLoadClasses(List<File> javaFiles) throws Exception
    {
//        compileJavaFiles(javaFiles, dependencies);
        List<Class<?>> loadedClasses = loadCompiledClasses(javaFiles);

        getLog().info("Loaded classes:");
        for (Class<?> clazz : loadedClasses)
            getLog().info(" - " + clazz.getName());

        return loadedClasses;
    }

    private void validateDirectoryParameters() throws MojoExecutionException
    {
        if (!sourceDirectory.exists())
        {
            getLog().warn("Source directory does not exist: " + sourceDirectory);
            throw new MojoExecutionException("Source directory does not exist: " + sourceDirectory);
        }

        // Compile Java files
        if (!outputDirectory.exists() && !outputDirectory.mkdirs())
            throw new MojoExecutionException("Failed to create output directory: " + outputDirectory);
    }

    private void findClassesInPackage(File dir, String packageName, List<File> results)
    {
        File[] files = dir.listFiles();
        if (files == null)
            return;

        String packagePath = packageName.replace(".", File.separator);

        for (File file : files)
        {
            if (file.isDirectory())
                findClassesInPackage(file, packageName, results);
            else
            {
                String fileAbsolutePath = file.getAbsolutePath();
                String directoryPath = fileAbsolutePath.substring(0, fileAbsolutePath.lastIndexOf(File.separator));
                if (file.getName().endsWith(".java") && file.getPath().contains(packagePath) && directoryPath.endsWith(packagePath))
                    results.add(file);
            }
        }
    }

    private void resolveProjectDependencies() throws MojoExecutionException
    {
        dependencies = new ArrayList<>();

        try
        {
            // 添加编译期依赖
            dependencies.addAll(project.getCompileClasspathElements());
            // 添加运行时依赖
            dependencies.addAll(project.getRuntimeClasspathElements());
            // 添加项目自身的 target/classes 目录
            dependencies.add(project.getBuild().getOutputDirectory());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Failed to resolve project dependencies", e);
        }

        getLog().info("Resolved dependencies for classpath:");
        for (String dependency : dependencies)
            getLog().info(" - " + dependency);
    }

    private void compileJavaFiles(List<File> javaFiles, List<String> dependencies) throws MojoExecutionException
    {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null)
            throw new MojoExecutionException("No Java compiler available. Ensure you're running the plugin with a JDK.");

        List<String> options = new ArrayList<>();
        options.add("-d");
        options.add(outputDirectory.getAbsolutePath());

        // Add dependencies to classpath
        if (!dependencies.isEmpty())
        {
            options.add("-classpath");
            options.add(String.join(File.pathSeparator, dependencies));
        }

        List<String> filePaths = new ArrayList<>();
        for (File file : javaFiles)
            filePaths.add(file.getAbsolutePath());

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(filePaths);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
        boolean success = task.call();

        if (!success)
        {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
                getLog().error(diagnostic.toString());

            throw new MojoExecutionException("Compilation failed. See diagnostics for details.");
        }
    }

    private List<Class<?>> loadCompiledClasses(List<File> javaFiles) throws Exception
    {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{outputDirectory.toURI().toURL()}, currentClassLoader);
        Thread.currentThread().setContextClassLoader(urlClassLoader);

        List<Class<?>> classes = new ArrayList<>();
        for (File file : javaFiles)
        {
            String className = getClassName(file);
            Class<?> clazz = urlClassLoader.loadClass(className);
            classes.add(clazz);
        }

        return classes;
    }

    private String getClassName(File javaFile)
    {
        String absolutePath = javaFile.getAbsolutePath();
        String sourceDirPath = sourceDirectory.getAbsolutePath();
        if (!absolutePath.startsWith(sourceDirPath))
            throw new IllegalStateException("Java file is not within the source directory.");

        String relativePath = absolutePath.substring(sourceDirPath.length() + 1); // Remove the source dir path
        return relativePath.replace(File.separator, ".").replace(".java", "");  // Convert path to fully qualified class name
    }

    private String getSchemaMigrationHistoryClassName()
    {
        Date now = new Date();
        String timestamp = DATE_FORMAT.format(now);
        return SCHEMA_MIGRATION + timestamp;
    }

    private String getCodeFilePath(String classSimpleName)
    {
        return sourceDirectory + File.separator + schemaPackage.replace(".", File.separator) + File.separator + classSimpleName + ".java";
    }

    private void writeCodeToClass(String className, String code) throws IOException
    {
        String codeFilePath = getCodeFilePath(className);
        Files.writeString(Path.of(codeFilePath), code, StandardCharsets.UTF_8);
    }
}
