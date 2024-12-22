package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;

@Data
public class RenameColumnOperation
{
    private String tableName;
    private String oldColumnName;
    private String newColumnName;
}
