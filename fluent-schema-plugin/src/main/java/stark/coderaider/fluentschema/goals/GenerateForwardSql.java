package stark.coderaider.fluentschema.goals;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;

@Mojo(name = "generate-forward-sql", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class GenerateForwardSql extends GoalBase
{
    @Parameter(property = "schemaPackage", required = true)
    private String schemaPackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.prepare();


    }

    private List<Class<?>> loadSchemaMigrationClasses()
    {
        List<File> classesInPackage = findClassesInPackage(schemaPackage);
//        loadCompiledClasses()
        return null;
    }
}
