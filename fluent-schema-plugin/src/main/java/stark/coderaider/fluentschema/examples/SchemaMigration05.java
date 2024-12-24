package stark.coderaider.fluentschema.examples;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;

import java.util.List;

public class SchemaMigration05 extends SchemaMigrationBase
{
    @Override
    public void forward()
    {
        forwardBuilder.addTable("t_add", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("birthday").type("DATETIME").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.engine("InnoDB");
        });
        forwardBuilder.dropTable("t_drop");
        forwardBuilder.renameTable("student", "teacher");
        forwardBuilder.alterColumn("t_alter_column_type", ColumnMetadata.builder().name("name").type("VARCHAR(100)").nullable(true).unique(false).build());
        forwardBuilder.dropKey("person", "idx_name");
        forwardBuilder.dropColumn("person", "name");
        forwardBuilder.addColumn("person", ColumnMetadata.builder().name("birth_date").type("DATETIME").nullable(true).unique(false).build());
        forwardBuilder.addKey("person", KeyMetadata.builder().name("idx_birth_date").columns(List.of("birth_date")).build());
        forwardBuilder.dropKey("t_alter_column", "idx_name");
        forwardBuilder.renameColumn("t_alter_column", "name", "name1");
        forwardBuilder.addKey("t_alter_column", KeyMetadata.builder().name("idx_name").columns(List.of("name1")).build());
    }

    @Override
    public void backward()
    {
        backwardBuilder.alterColumn("t_alter_column_type", ColumnMetadata.builder().name("name").type("VARCHAR(200)").nullable(true).unique(false).build());
        backwardBuilder.dropKey("person", "idx_birth_date");
        backwardBuilder.dropColumn("person", "birth_date");
        backwardBuilder.addColumn("person", ColumnMetadata.builder().name("name").type("VARCHAR(32767)").nullable(true).unique(false).build());
        backwardBuilder.addKey("person", KeyMetadata.builder().name("idx_name").columns(List.of("name")).build());
        backwardBuilder.dropKey("t_alter_column", "idx_name");
        backwardBuilder.renameColumn("t_alter_column", "name1", "name");
        backwardBuilder.addKey("t_alter_column", KeyMetadata.builder().name("idx_name").columns(List.of("name")).build());
        backwardBuilder.renameTable("teacher", "student");
        backwardBuilder.addTable("t_drop", builder ->
        {
            builder.column().name("id").type("BIGINT").nullable(false).unique(false);
            builder.column().name("name").type("VARCHAR(32767)").nullable(true).unique(false);
            builder.primaryKey().columnName("id");
            builder.key().name("idx_name").columns(List.of("name"));
            builder.engine("InnoDB");
        });
        backwardBuilder.dropTable("t_add");
    }
}