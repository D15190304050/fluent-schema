package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

@Data
public class AddColumnOperation
{
    private String tableName;
    private ColumnMetadata columnMetadata;
}
