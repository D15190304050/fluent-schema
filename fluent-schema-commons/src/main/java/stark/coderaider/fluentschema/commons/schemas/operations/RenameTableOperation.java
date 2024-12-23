package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RenameTableOperation extends MigrationOperationBase
{
    private String oldTableName;
    private String newTableName;

    @Override
    public String toSql()
    {
        return "";
    }
}
