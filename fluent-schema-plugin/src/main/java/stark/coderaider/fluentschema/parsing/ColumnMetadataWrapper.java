package stark.coderaider.fluentschema.parsing;

import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyBuilderInfo;
import stark.coderaider.fluentschema.commons.schemas.PrimaryKeyMetadata;

import java.util.List;

@Data
public class ColumnMetadataWrapper
{
    private ColumnMetadata columnMetadata;
    private PrimaryKeyMetadata primaryKeyMetadata;
    private List<KeyBuilderInfo> keyBuilderInfos;
}
