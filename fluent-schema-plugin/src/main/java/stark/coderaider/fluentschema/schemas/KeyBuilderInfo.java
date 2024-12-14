package stark.coderaider.fluentschema.schemas;

import lombok.Data;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;

import java.lang.reflect.Field;

@Data
public class KeyBuilderInfo
{
    private String key;
    private ColumnMetadata column;
    private int order;
    private Field field;
}
