package stark.coderaider.fluentschema.examples;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationInfo;
import stark.dataworks.basic.data.json.JsonSerializer;

import java.util.List;

public class SchemaMigrationExample extends SchemaMigrationBase
{
    @Override
    public void forward()
    {
        forwardBuilder.addColumn("person", ColumnMetadata.builder()
            .name("gender")
            .type("VARCHAR(200)")
            .comment("Gender of the person.")
            .build());

        forwardBuilder.dropColumn("person2", "birth_place");

        forwardBuilder.alterColumn("person2", ColumnMetadata.builder()
            .name("gender")
            .type("VARCHAR(200)")
            .build());

        forwardBuilder.renameColumn("person2", "birthday", "birthdate");

        forwardBuilder.dropTable("person3");

        forwardBuilder.addTable("blog", builder ->
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

        forwardBuilder.renameTable("person5", "person6");

        forwardBuilder.addKey("person6", KeyMetadata.builder()
            .name("k1")
            .columns(List.of("c1", "c2"))
            .build());

        forwardBuilder.dropKey("person6", "k2");
    }

    @Override
    public void backward()
    {
        backwardBuilder.dropColumn("person", "gender");

        backwardBuilder.addColumn("person2", ColumnMetadata.builder()
            .name("birth_place")
            .type("VARCHAR(200)")
            .build());

        backwardBuilder.alterColumn("person2", ColumnMetadata.builder()
            .name("gender")
            .type("VARCHAR(100)")
            .build());

        backwardBuilder.renameColumn("person2", "birthdate", "birthday");

        backwardBuilder.addTable("person3", builder ->
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

        backwardBuilder.dropTable("blog");

        backwardBuilder.renameTable("person6", "person5");

        backwardBuilder.dropKey("person6", "k1");

        backwardBuilder.addKey("person6", KeyMetadata.builder()
            .name("k2")
            .columns(List.of("c1", "c2"))
            .build());
    }

    public static void main(String[] args)
    {
        SchemaMigrationExample example = new SchemaMigrationExample();
        example.forward();
        example.backward();

        MigrationOperationInfo forwardOperationInfo = example.toForwardOperationInfo();
        System.out.println(JsonSerializer.serialize(forwardOperationInfo));

        MigrationOperationInfo backwardOperationInfo = example.toBackwardOperationInfo();
        System.out.println(JsonSerializer.serialize(backwardOperationInfo));
    }
}
