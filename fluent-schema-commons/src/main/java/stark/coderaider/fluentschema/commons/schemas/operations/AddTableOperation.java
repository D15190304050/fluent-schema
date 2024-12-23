package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddTableOperation extends MigrationOperationBase
{
    private TableSchemaInfo tableSchemaInfo;

    @Override
    public String toSql()
    {
        return "";
    }
}
