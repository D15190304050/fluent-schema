package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlterColumnOperation extends MigrationOperationBase
{
    private String tableName;
    private ColumnMetadata columnMetadata;

    @Override
    public String toSql()
    {
        return "";
    }
}
