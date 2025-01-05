package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Mojo(name = "generate-backward-sql", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class GenerateBackwardSql extends SqlGoalBase
{
    @Override
    public void execute()
    {
        try
        {
            super.prepare();

            getLog().info("sqlOutputFilePath = " + sqlOutputFilePath);
            String forwardSql = generateBackwardSql(backwardCount);
            Files.writeString(Path.of(sqlOutputFilePath), forwardSql, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }
}
