package stark.coderaider.fluentschema.commons.schemas;

import lombok.Data;

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
