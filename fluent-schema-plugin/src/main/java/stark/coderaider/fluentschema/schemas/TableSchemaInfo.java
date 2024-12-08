package stark.coderaider.fluentschema.schemas;

import lombok.Data;

import java.util.List;

@Data
public class TableSchemaInfo
{
    private String tableName;
    private List<ColumnInfo> columnInfos;
}
