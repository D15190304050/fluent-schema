package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RenameColumnOperation extends MigrationOperationBase
{
    private String tableName;
    private String oldColumnName;
    private String newColumnName;

    @Override
    public String toSql()
    {
        return "";
    }
}
