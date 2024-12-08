package stark.coderaider.fluentschema.schemas;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnInfo
{
    private String name;
    private String type;
    private boolean nullable;
    private String comment;
    private String defaultValue;
    private String onUpdate;
}
