package stark.coderaider.fluentschema.commons.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrimaryKeyMetadata
{
    private String columnName;
}
