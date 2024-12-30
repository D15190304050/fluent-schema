package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Mojo(name = "generate-forward-sql", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class GenerateForwardSql extends SqlGoalBase
{
    @Parameter(property = "sqlOutputFilePath", required = true)
    private String sqlOutputFilePath;

    @Override
    public void execute()
    {
        try
        {
            super.prepare();

            getLog().info("sqlOutputFilePath = " + sqlOutputFilePath);
            String forwardSql = generateForwardSql();
            Files.writeString(Path.of(sqlOutputFilePath), forwardSql, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }
}
