package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

@Mojo(name = "apply-schema-change", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class ApplySchemaChange extends SqlGoalBase
{
    @Override
    public void execute()
    {
        try
        {
            super.prepare();

            String forwardSql = generateForwardSql();
            List<String> commands = splitCommands(forwardSql);
            executeCommands(commands);
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }
}
