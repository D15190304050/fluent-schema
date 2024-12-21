package stark.coderaider.fluentschema.commons.schemas;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;

@Data
public class TableMetadata
{
    private String name;
    private NamingConvention namingConvention;
    private String engine;
    private String comment;
}
