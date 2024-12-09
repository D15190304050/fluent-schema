package stark.coderaider.fluentschema.schemas;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrimaryKeyInfo
{
    private String columnName;
}
