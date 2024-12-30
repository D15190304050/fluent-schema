package stark.coderaider.fluentschema.goals;

import com.mysql.cj.jdbc.Driver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Mojo(name = "apply-schema-change", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class ApplySchemaChange extends GoalBase
{
    @Parameter(property = "jdbcUrl")
    private String jdbcUrl;

    @Parameter(property = "username")
    private String username;

    @Parameter(property = "password")
    private String password;

    @Override
    public void execute()
    {
        try
        {
            super.prepare();


            Connection connection = getConnection();

            connection.close();
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException
    {
        Properties props = new Properties();
        props.setProperty("username", username);
        props.setProperty("password", password);
        Driver driver = new Driver();
        Connection connection = driver.connect(jdbcUrl, props);
        return connection;
    }
}
