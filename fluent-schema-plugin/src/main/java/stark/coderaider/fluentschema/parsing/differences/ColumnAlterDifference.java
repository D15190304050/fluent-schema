package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

@Data
public class ColumnAlterDifference
{
    private String name;
    private ColumnMetadata oldColumnMetadata;
    private ColumnMetadata newColumnMetadata;
}
