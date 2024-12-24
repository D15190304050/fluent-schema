package stark.coderaider.fluentschema.commons.schemas;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TableSchemaBuilder
{
    private final String name;
    private final List<ColumnMetadata.ColumnMetadataBuilder> columnMetadataBuilders;
    private PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKeyMetadataBuilder;
    private final List<KeyMetadata.KeyMetadataBuilder> keyMetadataBuilders;
    private String tableComment;
    private String engine;

    public TableSchemaBuilder(String name)
    {
        this.name = name;
        columnMetadataBuilders = new ArrayList<>();
        keyMetadataBuilders = new ArrayList<>();
        tableComment = null;
    }

    public TableSchemaInfo toTableSchemaInfo()
    {
        TableSchemaInfo tableSchemaInfo = new TableSchemaInfo();
        tableSchemaInfo.setName(name);
        tableSchemaInfo.setComment(tableComment);
        tableSchemaInfo.setEngine(engine);

        List<ColumnMetadata> columnMetadata = new ArrayList<>();
        columnMetadataBuilders.forEach(x -> columnMetadata.add(x.build()));
        tableSchemaInfo.setColumnMetadatas(columnMetadata);

        if (primaryKeyMetadataBuilder != null)
            tableSchemaInfo.setPrimaryKeyMetadata(primaryKeyMetadataBuilder.build());

        List<KeyMetadata> keyMetadata = new ArrayList<>();
        tableSchemaInfo.setKeyMetadatas(keyMetadata);
        if (!keyMetadataBuilders.isEmpty())
            keyMetadataBuilders.forEach(x -> keyMetadata.add(x.build()));

        return tableSchemaInfo;
    }

    public void comment(String comment)
    {
        tableComment = comment;
    }
    public void engine(String engine)
    {
        this.engine = engine;
    }

    public ColumnMetadata.ColumnMetadataBuilder column()
    {
        ColumnMetadata.ColumnMetadataBuilder builder = ColumnMetadata.builder();
        columnMetadataBuilders.add(builder);
        return builder;
    }

    public PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKey()
    {
        primaryKeyMetadataBuilder = PrimaryKeyMetadata.builder();
        return primaryKeyMetadataBuilder;
    }

    public KeyMetadata.KeyMetadataBuilder key()
    {
        KeyMetadata.KeyMetadataBuilder builder = KeyMetadata.builder();
        keyMetadataBuilders.add(builder);
        return builder;
    }

    public static TableSchemaInfo build(String name, Consumer<TableSchemaBuilder> consumer)
    {
        TableSchemaBuilder builder = new TableSchemaBuilder(name);
        consumer.accept(builder);
        return builder.toTableSchemaInfo();
    }
}
