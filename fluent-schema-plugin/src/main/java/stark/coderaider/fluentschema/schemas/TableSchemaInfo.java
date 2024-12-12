package stark.coderaider.fluentschema.schemas;

import lombok.Data;
import stark.coderaider.fluentschema.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.metadata.KeyMetadata;
import stark.coderaider.fluentschema.metadata.PrimaryKeyMetadata;

import java.util.List;

@Data
public class TableSchemaInfo
{
    private String tableName;
    private String comment;
    private List<ColumnMetadata> columnMetadata;
    private PrimaryKeyMetadata primaryKeyMetadata;
    private List<KeyMetadata> keyMetadata;
}
