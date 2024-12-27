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
    public static final String MIGRATION_TEMPLATE_SQL_FILE_NAME = "MigrationTemplate.sql";

    private final List<SchemaMigrationBase> schemaMigrations;

    public SqlGenerator(List<SchemaMigrationBase> schemaMigrations)
    {
        this.schemaMigrations = schemaMigrations;
    }

    public String generateMigrationSql() throws IOException, URISyntaxException
    {
        String migrationTemplate = getMigrationTemplate();
        StringBuilder migrationSqlBuilder = new StringBuilder();

        for (SchemaMigrationBase schemaMigration : schemaMigrations)
        {
            schemaMigration.resetForwardOperations();
            schemaMigration.forward();

            // Add table creation statement of schema_snapshot_history if the migration indicates not initialized.
            addInitializationOfSnapshotHistory(migrationSqlBuilder, schemaMigration.isInitialized());

            List<MigrationOperationBase> forwardOperations = schemaMigration.toForwardOperations();

            if (!forwardOperations.isEmpty())
            {
                StringBuilder subMigrationSqlBuilder = new StringBuilder();
                for (MigrationOperationBase forwardOperation : forwardOperations)
                {
                    subMigrationSqlBuilder
                        .append(forwardOperation.toSql())
                        .append(System.lineSeparator())
                        .append(System.lineSeparator());
                }

                Class<? extends SchemaMigrationBase> migrationClass = schemaMigration.getClass();
                String migrationClassName = migrationClass.getSimpleName();

                String migrationCommand = migrationTemplate.replace("#{sp_alter_tables}", "sp_alter_tables_" + migrationClassName);
                migrationCommand = migrationCommand.replace("#{schemaChanges}", subMigrationSqlBuilder);
                migrationCommand = migrationCommand.replace("#{snapshotName}", migrationClassName);

                migrationSqlBuilder
                    .append(migrationCommand)
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            }
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

    private String getMigrationTemplate() throws IOException, URISyntaxException
    {
        return getStringFromResourceFile(MIGRATION_TEMPLATE_SQL_FILE_NAME);
    }
}
