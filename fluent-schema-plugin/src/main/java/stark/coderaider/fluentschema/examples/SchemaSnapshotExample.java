package stark.coderaider.fluentschema.examples;

import stark.coderaider.fluentschema.schemas.SchemaSnapshotBase;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;
import stark.dataworks.basic.data.json.JsonSerializer;

import java.util.List;

public class SchemaSnapshotExample extends SchemaSnapshotBase
{
    @Override
    public void buildSchema()
    {
        schemaBuilder.table("blog", builder ->
        {
            builder.column()
                .name("id")
                .type("BIGINT")
                .comment("ID of the blog.")
                .autoIncrement(1, 1);

            builder.column()
                .name("title")
                .type("VARCHAR(255)")
                .defaultValue("")
                .onUpdate("")
                .comment("Title of the blog.");

            builder.primaryKey()
                .columnName("id");

            builder.key()
                .name("title")
                .columns(List.of("title"));

            builder.comment("Blogs.");
            builder.engine("InnoDB");
        });

        schemaBuilder.table("Person", builder ->
        {
            builder.column()
                .name("id")
                .type("BIGINT")
                .nullable(false)
                .unique(false)
                .autoIncrement(1, 1);
            builder.column()
                .name("name")
                .type("VARCHAR(200)")
                .nullable(true)
                .unique(false);
            builder.column()
                .name("gender")
                .type("VARCHAR(32767)")
                .nullable(true)
                .unique(false);
            builder.column()
                .name("birthday")
                .type("DATETIME")
                .nullable(true)
                .unique(false);
            builder.primaryKey()
                .columnName("id");
            builder.key()
                .name("idx_name")
                .columns(List.of("name"));
            builder.engine("InnoDB");
            builder.comment("Table of basic information of persons.");
        });
    }

    public static void main(String[] args)
    {
        SchemaSnapshotExample example = new SchemaSnapshotExample();
        example.buildSchema();
        List<TableSchemaInfo> tableSchemaInfos = example.getTableSchemaInfos();
        System.out.println(JsonSerializer.serialize(tableSchemaInfos));
    }
}
