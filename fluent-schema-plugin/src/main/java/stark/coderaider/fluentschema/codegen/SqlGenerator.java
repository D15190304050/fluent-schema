package stark.coderaider.fluentschema.codegen;

import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationBase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SqlGenerator
{
    public static final String SNAPSHOT_HISTORY_SQL_FILE_NAME = "schema_snapshot_history.sql";
    public static final String FORWARD_MIGRATION_TEMPLATE_SQL_FILE_NAME = "ForwardMigrationTemplate.sql";
    public static final String BACKWARD_MIGRATION_TEMPLATE_SQL_FILE_NAME = "BackwardMigrationTemplate.sql";

    private final List<SchemaMigrationBase> schemaMigrations;
    private final String version;

    public SqlGenerator(List<SchemaMigrationBase> schemaMigrations, String version)
    {
        this.schemaMigrations = schemaMigrations;
        this.version = version;
    }

    public String generateForwardMigrationSql() throws IOException, URISyntaxException
    {
        String forwardMigrationTemplate = getStringFromResourceFile(FORWARD_MIGRATION_TEMPLATE_SQL_FILE_NAME);
        StringBuilder migrationSqlBuilder = new StringBuilder();

        for (SchemaMigrationBase schemaMigration : schemaMigrations)
        {
            schemaMigration.resetForwardOperations();
            schemaMigration.forward();

            // Add table creation statement of schema_snapshot_history if the migration indicates not initialized.
            addInitializationOfSnapshotHistory(migrationSqlBuilder, schemaMigration.isInitialized());

            List<MigrationOperationBase> forwardOperations = schemaMigration.toForwardOperations();
            concatOperationSql(forwardMigrationTemplate, migrationSqlBuilder, schemaMigration, forwardOperations);
        }

        return migrationSqlBuilder.toString();
    }

    private void addInitializationOfSnapshotHistory(StringBuilder migrationSqlBuilder, boolean initialized) throws IOException, URISyntaxException
    {
        if (!initialized)
        {
            String initializationOfSnapshotHistory = getStringFromResourceFile(SNAPSHOT_HISTORY_SQL_FILE_NAME);
            migrationSqlBuilder
                .append(initializationOfSnapshotHistory)
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        }
    }

    private String getStringFromResourceFile(String fileName) throws IOException, URISyntaxException
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public String generateBackwardMigrationSql() throws IOException, URISyntaxException
    {
        String backwardMigrationTemplate = getStringFromResourceFile(BACKWARD_MIGRATION_TEMPLATE_SQL_FILE_NAME);
        StringBuilder migrationSqlBuilder = new StringBuilder();

        for (SchemaMigrationBase schemaMigration : schemaMigrations)
        {
            schemaMigration.resetBackwardOperations();
            schemaMigration.backward();

            List<MigrationOperationBase> backwardOperations = schemaMigration.toBackwardOperations();
            concatOperationSql(backwardMigrationTemplate, migrationSqlBuilder, schemaMigration, backwardOperations);
        }

        return migrationSqlBuilder.toString();
    }

    private void concatOperationSql(String migrationTemplate, StringBuilder migrationSqlBuilder, SchemaMigrationBase schemaMigration, List<MigrationOperationBase> operations)
    {
        if (!operations.isEmpty())
        {
            StringBuilder subMigrationSqlBuilder = new StringBuilder();
            for (MigrationOperationBase backwardOperation : operations)
            {
                subMigrationSqlBuilder
                    .append(backwardOperation.toSql())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            }

            Class<? extends SchemaMigrationBase> migrationClass = schemaMigration.getClass();
            String migrationClassName = migrationClass.getSimpleName();

            String migrationCommand = migrationTemplate.replace("#{sp_alter_tables}", "sp_alter_tables_" + migrationClassName);
            migrationCommand = migrationCommand.replace("#{schemaChanges}", subMigrationSqlBuilder);
            migrationCommand = migrationCommand.replace("#{snapshotName}", migrationClassName);
            migrationCommand = migrationCommand.replace("#{version}", version);

            migrationSqlBuilder
                .append(migrationCommand)
                .append(System.lineSeparator())
                .append(System.lineSeparator());
        }
    }
}
