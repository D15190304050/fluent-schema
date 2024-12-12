package stark.coderaider.fluentschema.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrimaryKeyMetadata
{
    private String columnName;
}
