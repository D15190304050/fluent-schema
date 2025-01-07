package stark.coderaider.fluentschema.goals;

import com.mysql.cj.jdbc.Driver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import stark.coderaider.fluentschema.codegen.SqlGenerator;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SqlGoalBase extends GoalBase
{
    public static final String BEGINNING_OF_DELIMITER = "DELIMITER $$";
    public static final String END_OF_DELIMITER = "DELIMITER ;";
    public static final String END_OF_PROCEDURE_CREATION_WITH_DELIMITER = "END $$";
    public static final String END_OF_PROCEDURE_CREATION = "END";

    public static final Pattern SCHEMA_MIGRATION_CLASS_NAME_PATTERN;

    @Parameter(property = "jdbcUrl", required = true)
    protected String jdbcUrl;

    @Parameter(property = "username", required = true)
    protected String username;

    @Parameter(property = "password", required = true)
    protected String password;

    @Parameter(property = "sqlOutputFilePath", required = true)
    protected String sqlOutputFilePath;

    @Parameter(property = "backwardCount", defaultValue = "1")
    protected int backwardCount;

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

    protected String generateForwardSql(List<Class<?>> schemaMigrationClasses) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, URISyntaxException
    {
        schemaMigrationClasses.sort(Comparator.comparing(Class::getSimpleName));
        SqlGenerator sqlGenerator = getSchemaMigrations(schemaMigrationClasses);
        return sqlGenerator.generateForwardMigrationSql();
    }

    protected String generateBackwardSql(List<Class<?>> schemaMigrationClasses, int count) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException, URISyntaxException
    {
        // Sort by creation time descending.
        List<Class<?>> migrationClassesToApply = schemaMigrationClasses
            .stream()
            .sorted((x, y) -> y.getSimpleName().compareTo(x.getSimpleName()))
            .limit(count)
            .toList();

        SqlGenerator sqlGenerator = getSchemaMigrations(migrationClassesToApply);
        return sqlGenerator.generateBackwardMigrationSql();
    }

    private SqlGenerator getSchemaMigrations(List<Class<?>> migrationClassesToApply) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, URISyntaxException
    {
        List<SchemaMigrationBase> schemaMigrations = new ArrayList<>();
        for (Class<?> clazz : migrationClassesToApply)
        {
            Constructor<?> constructor = clazz.getConstructor();
            SchemaMigrationBase schemaMigrationBase = (SchemaMigrationBase) constructor.newInstance();
            schemaMigrations.add(schemaMigrationBase);
        }

        return new SqlGenerator(schemaMigrations, version);
    }

    protected String generateForwardSql() throws MojoExecutionException, IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        List<Class<?>> schemaMigrationClasses = loadSchemaMigrationClasses();
        return generateForwardSql(schemaMigrationClasses);
    }

    protected String generateBackwardSql(int count) throws MojoExecutionException, IOException, URISyntaxException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException
    {
        List<Class<?>> schemaMigrationClasses = loadSchemaMigrationClasses();
        return generateBackwardSql(schemaMigrationClasses, count);
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

    private Connection getConnection() throws SQLException
    {
        Properties props = new Properties();
        props.setProperty("username", username);
        props.setProperty("password", password);
        Driver driver = new Driver();
        return driver.connect(jdbcUrl, props);
    }

    protected void executeCommands(List<String> commands) throws SQLException
    {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        for (String command : commands)
            statement.execute(command);

        connection.close();
    }
}
