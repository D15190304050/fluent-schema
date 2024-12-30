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
    public static final String BEGINNING_OF_DELIMITER = "DELIMITER $$";
    public static final String END_OF_DELIMITER = "DELIMITER ;";
    public static final String END_OF_PROCEDURE_CREATION_WITH_DELIMITER = "END $$";
    public static final String END_OF_PROCEDURE_CREATION = "END";

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

    protected List<String> splitCommands(String sql)
    {
        List<String> commandsToExecutePre = splitCommandsByDelimiter(sql);
        return splitCommandsBySemicolon(commandsToExecutePre);
    }

    private static List<String> splitCommandsBySemicolon(List<String> commandsToExecutePre)
    {
        List<String> commandsToExecute = new ArrayList<>();
        for (String cmd : commandsToExecutePre)
        {
            if (cmd.contains(BEGINNING_OF_DELIMITER))
                commandsToExecute.add(cmd
                    .replace(BEGINNING_OF_DELIMITER, "")
                    .replace(END_OF_DELIMITER, "")
                    .replace(END_OF_PROCEDURE_CREATION_WITH_DELIMITER, END_OF_PROCEDURE_CREATION)
                );
            else
            {
                String[] splitCommands = cmd.split(";");
                for (String splitCommand : splitCommands)
                {
                    if (!splitCommand.isBlank())
                        commandsToExecute.add(splitCommand);
                }
            }
        }
        return commandsToExecute;
    }

    private static List<String> splitCommandsByDelimiter(String sql)
    {
        List<String> commandsToExecutePre = new ArrayList<>();
        int startIndex = 0;
        while (startIndex < sql.length())
        {
            int indexOfDelimiterBegin = sql.indexOf(BEGINNING_OF_DELIMITER, startIndex);

            // No more DELIMITER $$, take the remaining part of the string.
            if (indexOfDelimiterBegin == -1)
            {
                commandsToExecutePre.add(sql.substring(startIndex));
                break;
            }

            // Add everything before the DELIMITER $$ block.
            if (indexOfDelimiterBegin > startIndex)
                commandsToExecutePre.add(sql.substring(startIndex, indexOfDelimiterBegin));

            int indexOfDelimiterEnd = sql.indexOf(END_OF_DELIMITER, indexOfDelimiterBegin);

            if (indexOfDelimiterEnd == -1)
            {
                commandsToExecutePre.add(sql.substring(indexOfDelimiterBegin));
                break;
            }

            commandsToExecutePre.add(sql.substring(indexOfDelimiterBegin, indexOfDelimiterEnd + END_OF_DELIMITER.length()));
            startIndex = indexOfDelimiterEnd + END_OF_DELIMITER.length();
        }
        return commandsToExecutePre;
    }
}
