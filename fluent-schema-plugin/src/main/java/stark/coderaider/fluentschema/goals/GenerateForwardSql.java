package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import stark.coderaider.fluentschema.codegen.SqlGenerator;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mojo(name = "generate-forward-sql", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
@Execute(phase = LifecyclePhase.COMPILE)
public class GenerateForwardSql extends GoalBase
{
    public static final Pattern SCHEMA_MIGRATION_CLASS_NAME_PATTERN;

    @Parameter(property = "sqlOutputFilePath", required = true)
    private String sqlOutputFilePath;

    static
    {
        SCHEMA_MIGRATION_CLASS_NAME_PATTERN = Pattern.compile("^" + SCHEMA_MIGRATION_CLASS_NAME_PREFIX + "\\d{14}$");
    }

    @Override
    public void execute()
    {
        try
        {
            super.prepare();

            getLog().info("sqlOutputFilePath = " + sqlOutputFilePath);
            List<Class<?>> schemaMigrationClasses = loadSchemaMigrationClasses();
            String forwardSql = getForwardSql(schemaMigrationClasses);
            Files.writeString(Path.of(sqlOutputFilePath), forwardSql, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            getLog().error(e);
            throw new RuntimeException(e);
        }
    }

    private List<Class<?>> loadSchemaMigrationClasses() throws MojoExecutionException
    {
        List<Class<?>> classesInSchemaPackage = loadClassesInSchemaPackage();

        List<Class<?>> schemaMigrationClasses = new ArrayList<>();
        for (Class<?> clazz : classesInSchemaPackage)
        {
            String classSimpleName = clazz.getSimpleName();
            Matcher matcher = SCHEMA_MIGRATION_CLASS_NAME_PATTERN.matcher(classSimpleName);
            if (matcher.matches())
            {
                getLog().info("Loaded schema migration class " + clazz.getName() + ".");
                schemaMigrationClasses.add(clazz);
            }
        }

        return schemaMigrationClasses;
    }

    private String getForwardSql(List<Class<?>> schemaMigrationClasses) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, URISyntaxException
    {
        schemaMigrationClasses.sort(Comparator.comparing(Class::getSimpleName));
        List<SchemaMigrationBase> schemaMigrations = new ArrayList<>();
        for (Class<?> clazz : schemaMigrationClasses)
        {
            Constructor<?> constructor = clazz.getConstructor();
            SchemaMigrationBase schemaMigrationBase = (SchemaMigrationBase) constructor.newInstance();
            schemaMigrations.add(schemaMigrationBase);
        }

        SqlGenerator sqlGenerator = new SqlGenerator(schemaMigrations);
        return sqlGenerator.generateMigrationSql();
    }
}
