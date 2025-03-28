package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

@Data
public class TableChangeDifference
{
    private String name;
    private TableSchemaInfo oldTableSchemaInfo;
    private TableSchemaInfo newTableSchemaInfo;
}
