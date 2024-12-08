package stark.coderaider.fluentschema.schemas;

import java.util.ArrayList;
import java.util.List;

public class TableSchemaInfoBuilder
{
    private final String name;
    private final List<ColumnInfo.ColumnInfoBuilder> columnInfoBuilderList;

    public TableSchemaInfoBuilder(String name)
    {
        this.name = name;
        columnInfoBuilderList = new ArrayList<>();
    }

    public TableSchemaInfo toTableSchemaInfo()
    {
        List<ColumnInfo> columnInfos = new ArrayList<>();
        columnInfoBuilderList.forEach(x -> columnInfos.add(x.build()));

        TableSchemaInfo tableSchemaInfo = new TableSchemaInfo();
        tableSchemaInfo.setTableName(name);
        tableSchemaInfo.setColumnInfos(columnInfos);
        return tableSchemaInfo;
    }

    public ColumnInfo.ColumnInfoBuilder column()
    {
        ColumnInfo.ColumnInfoBuilder builder = ColumnInfo.builder();
        columnInfoBuilderList.add(builder);
        return builder;
    }
}
