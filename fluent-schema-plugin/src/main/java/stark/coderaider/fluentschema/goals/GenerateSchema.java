package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.util.CollectionUtils;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "generate-schema", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true, requiresDependencyResolution = org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME)
public class GenerateSchema extends AbstractMojo
{
    public static final String DEFAULT_SCHEMA_NAME = "SchemaSnapshot";

    @Parameter(defaultValue = "${project.basedir}/src/main/java", readonly = true, required = true)
    private File sourceDirectory;

    @Parameter(property = "entityPackage", required = true)
    private String entityPackage;

    @Parameter(property = "schemaPackage")
    private String schemaPackage;

    @Parameter(property = "dataSourceName")
    private String dataSourceName;

    @Parameter(defaultValue = "${project.build.directory}/generated-classes", readonly = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    private List<String> dependencies;

    @Override
    public void execute() throws MojoExecutionException
    {
        validateDirectoryParameters();
        resolveProjectDependencies();

        List<Class<?>> entityClasses = loadEntityClasses();
        if (CollectionUtils.isEmpty(entityClasses))
        {
            getLog().info("No entity classes found.");
            return;
        }

        String schemaClassName = getSchemaClassName();
        getLog().info("Schema class name: " + schemaClassName);
    }

    private String getSchemaClassName()
    {
        if (schemaPackage == null)
            schemaPackage = entityPackage.substring(0, entityPackage.lastIndexOf('.')) + ".schemas";

        getLog().info("Schema package: " + schemaPackage);
        String dataSourceClassName = convertDataSourceNameToClassName();
        return schemaPackage + "." + dataSourceClassName + DEFAULT_SCHEMA_NAME;
    }

    private String convertDataSourceNameToClassName()
    {
        String schemaPrefix = "";
        if (dataSourceName != null)
        {
            List<Character> charsToKeep = new ArrayList<>();
            for (char c : dataSourceName.toCharArray())
            {
                if (Character.isLetterOrDigit(c))
                    charsToKeep.add(c);
            }

            char[] charsToKeepArray = new char[charsToKeep.size()];
            for (int i = 0; i < charsToKeepArray.length; i++)
                charsToKeepArray[i] = charsToKeep.get(i);
            schemaPrefix = new String(charsToKeepArray);

            schemaPrefix = Character.toUpperCase(schemaPrefix.charAt(0)) + schemaPrefix.substring(1);
        }

        return schemaPrefix;
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

    private List<Class<?>> loadSchemaClasses() throws MojoExecutionException
    {
        getLog().info("Analyzing sources in directory: " + schemaPackage);

        List<File> javaFiles = findClassesInPackage(schemaPackage);
        if (javaFiles.isEmpty())
            return new ArrayList<>();

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
        compileJavaFiles(javaFiles, dependencies);
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
                String directoryPath = fileAbsolutePath.substring(0, fileAbsolutePath.lastIndexOf("\\"));
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
        List<Class<?>> classes = new ArrayList<>();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{outputDirectory.toURI().toURL()});

        for (File file : javaFiles)
        {
            String className = getClassName(file);
            Class<?> clazz = classLoader.loadClass(className);
            classes.add(clazz);
        }

        classLoader.close();
        return classes;
    }

    private String getClassName(File javaFile) throws Exception
    {
        String absolutePath = javaFile.getAbsolutePath();
        String sourceDirPath = sourceDirectory.getAbsolutePath();
        if (!absolutePath.startsWith(sourceDirPath))
            throw new IllegalStateException("Java file is not within the source directory.");

        String relativePath = absolutePath.substring(sourceDirPath.length() + 1); // Remove the source dir path
        return relativePath.replace(File.separator, ".").replace(".java", "");  // Convert path to fully qualified class name
    }
}
