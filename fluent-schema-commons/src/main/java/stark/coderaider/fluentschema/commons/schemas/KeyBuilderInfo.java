package stark.coderaider.fluentschema.commons.schemas;

import lombok.Data;

import java.lang.reflect.Field;

@Data
public class KeyBuilderInfo
{
    private String key;
    private ColumnMetadata column;
    private int order;
    private Field field;
}
