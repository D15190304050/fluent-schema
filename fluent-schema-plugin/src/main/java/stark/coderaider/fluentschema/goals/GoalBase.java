package stark.coderaider.fluentschema.goals;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public abstract class GoalBase extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    @Parameter(property = "schemaPackage", required = true)
    protected String schemaPackage;

    @Parameter(property = "dataSourceName")
    protected String dataSourceName;

    protected File sourceDirectory;
    protected File outputDirectory;


    protected void prepare() throws MojoExecutionException
    {
        String sourceDirectoryPath = session.getCurrentProject().getBuild().getSourceDirectory();
        sourceDirectory = new File(sourceDirectoryPath);

        String outputDirectoryPath = session.getCurrentProject().getBuild().getOutputDirectory();
        outputDirectory = new File(outputDirectoryPath);
    }

    protected List<File> findClassesInPackage(String packageName)
    {
        List<File> javaFiles = new ArrayList<>();
        findClassesInPackage(sourceDirectory, packageName, javaFiles);
        return javaFiles;
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

    protected List<Class<?>> loadCompiledClasses(List<File> javaFiles) throws Exception
    {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{outputDirectory.toURI().toURL()}, currentClassLoader);
        Thread.currentThread().setContextClassLoader(urlClassLoader);

        List<Class<?>> loadedClasses = new ArrayList<>();
        for (File file : javaFiles)
        {
            String className = getClassName(file);
            Class<?> clazz = urlClassLoader.loadClass(className);
            loadedClasses.add(clazz);
        }

        getLog().info("Loaded classes:");
        for (Class<?> clazz : loadedClasses)
            getLog().info(" - " + clazz.getName());

        return loadedClasses;
    }

    /**
     * Returns the fully qualified class name represented by the file.
     * @param javaFile The .java file.
     * @return The fully qualified class name represented by the file.
     */
    private String getClassName(File javaFile)
    {
        String absolutePath = javaFile.getAbsolutePath();
        String sourceDirPath = sourceDirectory.getAbsolutePath();
        if (!absolutePath.startsWith(sourceDirPath))
            throw new IllegalStateException("Java file is not within the source directory.");

        // Remove the source dir path.
        String relativePath = absolutePath.substring(sourceDirPath.length() + 1);

        // Convert path to fully qualified class name.
        return relativePath.replace(File.separator, ".").replace(".java", "");
    }
}
