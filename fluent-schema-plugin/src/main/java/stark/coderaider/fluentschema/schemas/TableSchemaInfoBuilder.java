package stark.coderaider.fluentschema.schemas;

import java.util.ArrayList;
import java.util.List;

public class TableSchemaInfoBuilder
{
    private final String name;
    private final List<ColumnInfo.ColumnInfoBuilder> columnInfoBuilders;
    private PrimaryKeyInfo.PrimaryKeyInfoBuilder primaryKeyInfoBuilder;
    private final List<KeyInfo.KeyInfoBuilder> keyInfoBuilders;
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

        List<ColumnInfo> columnInfos = new ArrayList<>();
        columnInfoBuilders.forEach(x -> columnInfos.add(x.build()));
        tableSchemaInfo.setColumnInfos(columnInfos);

        if (primaryKeyInfoBuilder != null)
            tableSchemaInfo.setPrimaryKeyInfo(primaryKeyInfoBuilder.build());

        if (!keyInfoBuilders.isEmpty())
        {
            List<KeyInfo> keyInfos = new ArrayList<>();
            keyInfoBuilders.forEach(x -> keyInfos.add(x.build()));
            tableSchemaInfo.setKeyInfos(keyInfos);
        }

        return tableSchemaInfo;
    }

    public void comment(String comment)
    {
        tableComment = comment;
    }

    public ColumnInfo.ColumnInfoBuilder column()
    {
        ColumnInfo.ColumnInfoBuilder builder = ColumnInfo.builder();
        columnInfoBuilders.add(builder);
        return builder;
    }

    public PrimaryKeyInfo.PrimaryKeyInfoBuilder primaryKey()
    {
        primaryKeyInfoBuilder = PrimaryKeyInfo.builder();
        return primaryKeyInfoBuilder;
    }

    public KeyInfo.KeyInfoBuilder key()
    {
        KeyInfo.KeyInfoBuilder builder = KeyInfo.builder();
        keyInfoBuilders.add(builder);
        return builder;
    }
}
