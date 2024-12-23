package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DropTableOperation extends MigrationOperationBase
{
    private String tableName;

    @Override
    public String toSql()
    {
        return "";
    }
}
