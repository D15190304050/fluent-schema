package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

@Data
public class AddKeyOperation
{
    private String tableName;
    private KeyMetadata keyMetadata;
}
