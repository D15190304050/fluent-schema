package stark.coderaider.fluentschema.schemas;

import lombok.Data;

@Data
public class TypeInfo
{
    private String type;
    private boolean nullable;
    private String length;
    private String defaultValue;
    private String comment;
}
