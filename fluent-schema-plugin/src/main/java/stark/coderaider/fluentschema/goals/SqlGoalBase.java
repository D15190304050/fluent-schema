package stark.coderaider.fluentschema.goals;

import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.codegen.SqlGenerator;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SqlGoalBase extends GoalBase
{
    public static final Pattern SCHEMA_MIGRATION_CLASS_NAME_PATTERN;

    static
    {
        SCHEMA_MIGRATION_CLASS_NAME_PATTERN = Pattern.compile("^" + SCHEMA_MIGRATION_CLASS_NAME_PREFIX + "\\d{14}$");
    }

    protected List<Class<?>> loadSchemaMigrationClasses() throws MojoExecutionException
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

    protected String getForwardSql(List<Class<?>> schemaMigrationClasses) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, URISyntaxException
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

    protected String generateForwardSql() throws MojoExecutionException, IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        List<Class<?>> schemaMigrationClasses = loadSchemaMigrationClasses();
        return getForwardSql(schemaMigrationClasses);
    }
}
