package stark.coderaider.fluentschema.examples;

import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationBase;

import java.util.List;

public class SchemaMigration06 extends SchemaMigrationBase
{
    @Override
    public void forward()
    {
        forwardBuilder.createTable("t_alter_column_type", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("name").type("VARCHAR(100)").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.key().name("idx_name").columns(List.of("name"));
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("teacher", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("school_id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("name").type("VARCHAR(32767)").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("person", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("birth_date").type("DATETIME").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.key().name("idx_birth_date").columns(List.of("birth_date"));
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("t_add", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("birthday").type("DATETIME").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.createTable("t_alter_column", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("name1").type("VARCHAR(32767)").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.key().name("idx_name").columns(List.of("name1"));
            builder.engine("InnoDB");
        });
    }

    @Override
    public void backward()
    {
        backwardBuilder.dropTable("t_alter_column_type");
        backwardBuilder.dropTable("teacher");
        backwardBuilder.dropTable("person");
        backwardBuilder.dropTable("t_add");
        backwardBuilder.dropTable("t_alter_column");
    }

    public static void main(String[] args)
    {
        SchemaMigration06 schemaMigration06 = new SchemaMigration06();
        schemaMigration06.forward();
        List<MigrationOperationBase> forwardOperations = schemaMigration06.toForwardOperations();
        for (MigrationOperationBase forwardOperation : forwardOperations)
            System.out.println(forwardOperation.toSql());
    }
}