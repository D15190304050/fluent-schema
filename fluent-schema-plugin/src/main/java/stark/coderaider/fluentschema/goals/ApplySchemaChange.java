package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

@Mojo(name = "apply-schema-change", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, aggregator = true)
//@Execute(phase = LifecyclePhase.COMPILE)
public class ApplySchemaChange extends GoalBase
{
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.prepare();

        // 在目标模块目录下定位配置文件 application.yaml
//        File yamlFile = new File(targetModule.getBasedir(), "src/main/resources/application.yaml");
//        if (!yamlFile.exists()) {
//            throw new MojoExecutionException("配置文件不存在: " + yamlFile.getAbsolutePath());
//        }
//
//        getLog().info("找到配置文件: " + yamlFile.getAbsolutePath());
    }
}
