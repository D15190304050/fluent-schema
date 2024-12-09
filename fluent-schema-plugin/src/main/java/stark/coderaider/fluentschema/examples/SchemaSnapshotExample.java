package stark.coderaider.fluentschema.examples;

import stark.coderaider.fluentschema.schemas.AutoIncrementInfo;
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
                .comment("Title of the blog.");

            builder.primaryKey()
                .columnName("id");

            builder.key()
                .name("title")
                .columns(List.of("title"));

            builder.comment("Blogs.");
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
