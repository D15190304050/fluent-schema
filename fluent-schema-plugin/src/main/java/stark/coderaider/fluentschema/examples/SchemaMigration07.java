package stark.coderaider.fluentschema.examples;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationBase;

import java.util.List;

public class SchemaMigration07 extends SchemaMigrationBase
{
    @Override
    public void forward()
    {
        forwardBuilder.addTable("schema_snapshot_history", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false).autoIncrement(1);
            builder.column().name("schema_snapshot_name").type("VARCHAR(200)").nullable(false).unique(true)
                .comment("ID of the schema snapshot.");
            builder.column().name("fluent_schema_version").type("VARCHAR(200)").nullable(false).unique(false)
                .comment("Version of fluent schema which generates the schema snapshot history.");
            builder.primaryKey().columnName("id");
            builder.key().name("idx_schema_snapshot_name").columns(List.of("schema_snapshot_name"));
            builder.engine("InnoDB");
            builder.comment("History (tracking) of table schema change produced by fluent schema.");
        });
    }

    @Override
    public void backward()
    {
        backwardBuilder.dropTable("schema_snapshot_history");
    }

    public static void main(String[] args)
    {
        SchemaMigration07 schemaMigration = new SchemaMigration07();
        schemaMigration.forward();
        List<MigrationOperationBase> forwardOperations = schemaMigration.toForwardOperations();
        for (MigrationOperationBase forwardOperation : forwardOperations)
            System.out.println(forwardOperation.toSql());
    }
}