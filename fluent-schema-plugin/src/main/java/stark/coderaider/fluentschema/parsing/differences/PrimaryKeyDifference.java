package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.PrimaryKeyMetadata;

@Data
public class PrimaryKeyDifference
{
    private PrimaryKeyMetadata oldPrimaryKey;
    private PrimaryKeyMetadata newPrimaryKey;
}
