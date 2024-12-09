package stark.coderaider.fluentschema.schemas;

import lombok.Data;

import java.util.List;

@Data
public class TableSchemaInfo
{
    private String tableName;
    private String comment;
    private List<ColumnInfo> columnInfos;
    private PrimaryKeyInfo primaryKeyInfo;
    private List<KeyInfo> keyInfos;
}
