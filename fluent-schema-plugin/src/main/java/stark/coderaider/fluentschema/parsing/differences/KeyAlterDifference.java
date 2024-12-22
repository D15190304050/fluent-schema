package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

@Data
public class KeyAlterDifference
{
    private String name;
    private KeyMetadata oldKeyMetadata;
    private KeyMetadata newKeyMetadata;
}
