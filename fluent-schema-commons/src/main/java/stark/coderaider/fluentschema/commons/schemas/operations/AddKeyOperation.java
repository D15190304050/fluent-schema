package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddKeyOperation extends MigrationOperationBase
{
    private String tableName;
    private KeyMetadata keyMetadata;

    @Override
    public String toSql()
    {
        return "";
    }
}
