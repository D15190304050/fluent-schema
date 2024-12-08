package stark.coderaider.fluentschema;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.tools.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "analyze-sources", defaultPhase = LifecyclePhase.PROCESS_SOURCES, threadSafe = true, requiresDependencyResolution = org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME)
public class AnalyzeSourcesMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project.basedir}/src/main/java", readonly = true, required = true)
    private File sourceDirectory;

    @Parameter(property = "targetPackage", required = true)
    private String targetPackage;

    @Parameter(defaultValue = "${project.build.directory}/generated-classes", readonly = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException
    {
        if (!sourceDirectory.exists())
        {
            getLog().warn("Source directory does not exist: " + sourceDirectory);
            return;
        }

        getLog().info("Analyzing sources in directory: " + sourceDirectory);

        // Collect .java files in the target package
        List<File> javaFiles = new ArrayList<>();
        findClassesInPackage(sourceDirectory, targetPackage, javaFiles);

        if (javaFiles.isEmpty())
        {
            getLog().warn("No Java files found in the specified package: " + targetPackage);
            return;
        }

        // Compile Java files
        if (!outputDirectory.exists() && !outputDirectory.mkdirs())
            throw new MojoExecutionException("Failed to create output directory: " + outputDirectory);

        try
        {
            List<String> dependencies = resolveProjectDependencies();
            compileJavaFiles(javaFiles, dependencies);

            // Load classes
            List<Class<?>> loadedClasses = loadCompiledClasses(javaFiles);
            getLog().info("Loaded classes:");
            for (Class<?> clazz : loadedClasses)
                getLog().info(" - " + clazz.getName());
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error during class loading or compilation", e);
        }
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

    private List<String> resolveProjectDependencies() throws MojoExecutionException
    {
        List<String> dependencies = new ArrayList<>();

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

        return dependencies;
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
