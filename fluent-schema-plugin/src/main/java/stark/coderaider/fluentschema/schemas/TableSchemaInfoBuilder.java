package stark.coderaider.fluentschema.schemas;

import stark.coderaider.fluentschema.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.metadata.KeyMetadata;
import stark.coderaider.fluentschema.metadata.PrimaryKeyMetadata;

import java.util.ArrayList;
import java.util.List;

public class TableSchemaInfoBuilder
{
    private final String name;
    private final List<ColumnMetadata.ColumnMetadataBuilder> columnInfoBuilders;
    private PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKeyInfoBuilder;
    private final List<KeyMetadata.KeyMetadataBuilder> keyInfoBuilders;
    private String tableComment;

    public TableSchemaInfoBuilder(String name)
    {
        this.name = name;
        columnInfoBuilders = new ArrayList<>();
        keyInfoBuilders = new ArrayList<>();
        tableComment = null;
    }

    public TableSchemaInfo toTableSchemaInfo()
    {
        TableSchemaInfo tableSchemaInfo = new TableSchemaInfo();
        tableSchemaInfo.setTableName(name);
        tableSchemaInfo.setComment(tableComment);

        List<ColumnMetadata> columnMetadata = new ArrayList<>();
        columnInfoBuilders.forEach(x -> columnMetadata.add(x.build()));
        tableSchemaInfo.setColumnMetadata(columnMetadata);

        if (primaryKeyInfoBuilder != null)
            tableSchemaInfo.setPrimaryKeyMetadata(primaryKeyInfoBuilder.build());

        if (!keyInfoBuilders.isEmpty())
        {
            List<KeyMetadata> keyMetadata = new ArrayList<>();
            keyInfoBuilders.forEach(x -> keyMetadata.add(x.build()));
            tableSchemaInfo.setKeyMetadata(keyMetadata);
        }

        return tableSchemaInfo;
    }

    public void comment(String comment)
    {
        tableComment = comment;
    }

    public ColumnMetadata.ColumnMetadataBuilder column()
    {
        ColumnMetadata.ColumnMetadataBuilder builder = ColumnMetadata.builder();
        columnInfoBuilders.add(builder);
        return builder;
    }

    public PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKey()
    {
        primaryKeyInfoBuilder = PrimaryKeyMetadata.builder();
        return primaryKeyInfoBuilder;
    }

    public KeyMetadata.KeyMetadataBuilder key()
    {
        KeyMetadata.KeyMetadataBuilder builder = KeyMetadata.builder();
        keyInfoBuilders.add(builder);
        return builder;
    }
}
