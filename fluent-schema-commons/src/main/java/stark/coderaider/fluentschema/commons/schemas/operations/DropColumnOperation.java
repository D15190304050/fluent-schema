package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DropColumnOperation extends MigrationOperationBase
{
    private String tableName;
    private String columnName;

    @Override
    public String toSql()
    {
        return "";
    }
}
