package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Mojo(name = "apply-schema-change", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class ApplySchemaChange extends GoalBase
{
    @Parameter(property = "mainClass")
    private String mainClassName;

    private Class<?> mainClass;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            super.prepare();
            initializeClassLoader();
            loadMainClass();

//            ConfigurableApplicationContext applicationContext = SpringApplication.run(mainClass);
//            DataSource dataSource = applicationContext.getBean("dataSource", DataSource.class);
//            getLog().info("JDBC URL: " + dataSource.getConnection().getMetaData().getURL());
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }

    private void initializeClassLoader() throws MalformedURLException
    {
        String mainModuleSourceDirectoryPath = mainModule.getBuild().getOutputDirectory();
        File mainModuleSourceDirectory = new File(mainModuleSourceDirectoryPath);
        urlClassLoader = new URLClassLoader(new URL[]{mainModuleSourceDirectory.toURI().toURL()}, urlClassLoader);
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }

    private void loadMainClass() throws Exception
    {
        try
        {
            mainClass = urlClassLoader.loadClass(mainClassName);
            getLog().info("Loaded main class " + mainClassName);
        }
        catch (ClassNotFoundException e)
        {
            throw new MojoExecutionException("Unable to load main class " + mainClassName, e);
        }
    }
}
