package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

@Mojo(name = "rollback-schema-change", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class RollbackSchemaChange extends SqlGoalBase
{
    @Override
    public void execute()
    {
        try
        {
            super.prepare();

            String backwardSql = generateBackwardSql(backwardCount);
            List<String> commands = splitCommands(backwardSql);
            executeCommands(commands);
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }
}
