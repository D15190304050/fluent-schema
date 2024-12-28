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
    @Parameter(property = "domainModuleName")
    private String domainModuleName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.prepare();

        // 获取所有的子模块
        List<MavenProject> subModules = baseProject.getCollectedProjects();

        // 遍历查找目标模块
        MavenProject targetModule = null;
        for (MavenProject subModule : subModules)
        {
            getLog().info("发现子模块: " + subModule.getArtifactId());

            if (subModule.getArtifactId().equals(domainModuleName))
            {
                targetModule = subModule;
            }
        }

        // 打印模块信息
        if (targetModule != null)
        {
            getLog().info("找到目标模块: " + targetModule.getArtifactId());
            getLog().info("模块路径: " + targetModule.getBasedir().getAbsolutePath());
        }

        // 在目标模块目录下定位配置文件 application.yaml
//        File yamlFile = new File(targetModule.getBasedir(), "src/main/resources/application.yaml");
//        if (!yamlFile.exists()) {
//            throw new MojoExecutionException("配置文件不存在: " + yamlFile.getAbsolutePath());
//        }
//
//        getLog().info("找到配置文件: " + yamlFile.getAbsolutePath());
    }
}
