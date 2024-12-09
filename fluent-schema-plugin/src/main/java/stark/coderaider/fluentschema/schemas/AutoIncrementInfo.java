package stark.coderaider.fluentschema.schemas;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutoIncrementInfo
{
    private int begin;
    private int increment;
}
