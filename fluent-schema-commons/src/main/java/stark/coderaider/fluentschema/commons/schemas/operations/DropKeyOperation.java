package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

@Data
public class DropKeyOperation
{
    private String tableName;
    private String keyName;
}
