package stark.coderaider.fluentschema.metadata;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KeyMetadata
{
    private String name;
    private List<String> columns;
}
