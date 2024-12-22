package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;

@Data
public class DropColumnOperation
{
    private String tableName;
    private String columnName;
}
