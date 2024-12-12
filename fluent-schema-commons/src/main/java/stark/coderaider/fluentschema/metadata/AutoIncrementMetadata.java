package stark.coderaider.fluentschema.metadata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutoIncrementMetadata
{
    private int begin;
    private int increment;
}
