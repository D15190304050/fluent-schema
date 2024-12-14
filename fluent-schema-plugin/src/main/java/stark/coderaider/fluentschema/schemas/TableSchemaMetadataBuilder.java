package stark.coderaider.fluentschema.schemas;

import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.commons.metadata.KeyMetadata;
import stark.coderaider.fluentschema.commons.metadata.PrimaryKeyMetadata;

import java.util.ArrayList;
import java.util.List;

public class TableSchemaMetadataBuilder
{
    private final String name;
    private final List<ColumnMetadata.ColumnMetadataBuilder> columnMetadataBuilders;
    private PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKeyMetadataBuilder;
    private final List<KeyMetadata.KeyMetadataBuilder> keyMetadataBuilders;
    private String tableComment;

    public TableSchemaMetadataBuilder(String name)
    {
        this.name = name;
        columnMetadataBuilders = new ArrayList<>();
        keyMetadataBuilders = new ArrayList<>();
        tableComment = null;
    }

    public TableSchemaMetadata toTableSchemaInfo()
    {
        TableSchemaMetadata tableSchemaMetadata = new TableSchemaMetadata();
        tableSchemaMetadata.setName(name);
        tableSchemaMetadata.setComment(tableComment);

        List<ColumnMetadata> columnMetadata = new ArrayList<>();
        columnMetadataBuilders.forEach(x -> columnMetadata.add(x.build()));
        tableSchemaMetadata.setColumnMetadatas(columnMetadata);

        if (primaryKeyMetadataBuilder != null)
            tableSchemaMetadata.setPrimaryKeyMetadata(primaryKeyMetadataBuilder.build());

        if (!keyMetadataBuilders.isEmpty())
        {
            List<KeyMetadata> keyMetadata = new ArrayList<>();
            keyMetadataBuilders.forEach(x -> keyMetadata.add(x.build()));
            tableSchemaMetadata.setKeyMetadatas(keyMetadata);
        }

        return tableSchemaMetadata;
    }

    public void comment(String comment)
    {
        tableComment = comment;
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
}
