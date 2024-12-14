package stark.coderaider.fluentschema.schemas;

import lombok.Data;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.commons.metadata.KeyMetadata;
import stark.coderaider.fluentschema.commons.metadata.PrimaryKeyMetadata;

import java.util.List;

@Data
public class TableSchemaMetadata
{
    private String name;
    private String comment;
    private List<ColumnMetadata> columnMetadatas;
    private PrimaryKeyMetadata primaryKeyMetadata;
    private List<KeyMetadata> keyMetadatas;
}
