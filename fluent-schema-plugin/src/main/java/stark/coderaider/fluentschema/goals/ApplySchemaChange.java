package stark.coderaider.fluentschema.goals;

import com.mysql.cj.jdbc.Driver;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

@Mojo(name = "apply-schema-change", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class ApplySchemaChange extends SqlGoalBase
{
    @Parameter(property = "jdbcUrl", required = true)
    private String jdbcUrl;

    @Parameter(property = "username", required = true)
    private String username;

    @Parameter(property = "password", required = true)
    private String password;

    @Override
    public void execute()
    {
        try
        {
            super.prepare();

            String forwardSql = generateForwardSql();
            List<String> commands = splitCommands(forwardSql);

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            for (String command : commands)
                statement.execute(command);

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
        return driver.connect(jdbcUrl, props);
    }
}
