package stark.coderaider.fluentschema.schemas;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KeyInfo
{
    private String name;
    private List<String> columns;
}
