package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

import java.util.List;

@Data
public class KeyMetadataDifference
{
    private List<KeyMetadata> keysToDrop;
    private List<KeyMetadata> keysToAdd;
    private List<KeyAlterDifference> keysToAlter;
}
