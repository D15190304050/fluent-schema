package stark.coderaider.fluentschema.schemas;

import lombok.Data;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.commons.metadata.KeyMetadata;
import stark.coderaider.fluentschema.commons.metadata.PrimaryKeyMetadata;

import java.util.List;

@Data
public class TableSchemaInfo
{
    private String name;
    private String comment;
    private String engine;
    private PrimaryKeyMetadata primaryKeyMetadata;
    private List<ColumnMetadata> columnMetadatas;
    private List<KeyMetadata> keyMetadatas;
}
