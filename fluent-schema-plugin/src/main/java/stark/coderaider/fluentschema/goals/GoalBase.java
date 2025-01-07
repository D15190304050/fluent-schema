package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class GoalBase extends AbstractMojo
{
    public static final String PLUGIN_HELP_FILE_PATH = "/META-INF/maven/stark.coderaider/fluent-schema-plugin/pom.properties";
    public static final String SCHEMA_MIGRATION_CLASS_NAME_PREFIX = "SchemaMigration";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject baseProject;

    @Parameter(property = "schemaPackage", required = true)
    protected String schemaPackage;

    @Parameter(property = "domainModuleName")
    private String domainModuleName;

    @Parameter(property = "dataSourceName")
    protected String dataSourceName;

    protected MavenProject domainModule;
    protected File sourceDirectory;
    protected File outputDirectory;
    protected URLClassLoader urlClassLoader;
    protected String version;

    protected void prepare() throws MojoExecutionException, MalformedURLException
    {
        version = getPluginVersion();
        getLog().info("Version: " + version);

        prepareModules();

        String sourceDirectoryPath = domainModule.getBuild().getSourceDirectory();
        sourceDirectory = new File(sourceDirectoryPath);

        String outputDirectoryPath = domainModule.getBuild().getOutputDirectory();
        outputDirectory = new File(outputDirectoryPath);

        initializeClassLoader();
    }

    public String getPluginVersion()
    {
        Properties properties = new Properties();

        try (InputStream is = getClass().getResourceAsStream(PLUGIN_HELP_FILE_PATH))
        {
            if (is != null)
            {
                properties.load(is);
                return properties.getProperty("version", "unknown");
            }
        }
        catch (IOException e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }

        return "unknown";
    }

    private void initializeClassLoader() throws MalformedURLException
    {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        urlClassLoader = new URLClassLoader(new URL[]{outputDirectory.toURI().toURL()}, currentClassLoader);
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }

    private void prepareModules() throws MojoExecutionException
    {
        domainModule = findModuleByName(domainModuleName);
    }

    protected MavenProject findModuleByName(String moduleName) throws MojoExecutionException
    {
        if (!StringUtils.hasText(moduleName))
            return baseProject;

        List<MavenProject> subModules = baseProject.getCollectedProjects();
        for (MavenProject subModule : subModules)
        {
            if (subModule.getArtifactId().equals(moduleName))
            {
                getLog().info("Found module: " + moduleName);
                return subModule;
            }
        }

        throw new MojoExecutionException("No such module: " + moduleName + ", please check your configuration.");
    }

    protected List<File> findClassesInPackage(File sourceDirectory, String packageName)
    {
        List<File> javaFiles = new ArrayList<>();
        findClassesInPackage(sourceDirectory, packageName, javaFiles);
        return javaFiles;
    }

    protected List<File> findClassesInPackage(String packageName)
    {
        return findClassesInPackage(sourceDirectory, packageName);
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
        getLog().info("Loaded classes:");
        List<Class<?>> loadedClasses = new ArrayList<>();
        for (File file : javaFiles)
        {
            String className = getClassName(file);
            Class<?> clazz = urlClassLoader.loadClass(className);
            loadedClasses.add(clazz);
            getLog().info(" - " + clazz.getName());
        }

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

    protected List<Class<?>> loadClassesInSchemaPackage() throws MojoExecutionException
    {
        List<File> javaFiles = findClassesInPackage(schemaPackage);
        if (javaFiles.isEmpty())
            return new ArrayList<>();

        try
        {
            return loadCompiledClasses(javaFiles);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error during class loading or compilation", e);
        }
    }
}
