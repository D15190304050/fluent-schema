package stark.coderaider.fluentschema.commons.schemas;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnMetadata
{
    private String name;
    private String type;
    private boolean nullable;
    private boolean unique;
    private String comment;
    private String defaultValue;
    private String onUpdate;
    private AutoIncrementMetadata autoIncrement;

    public static class ColumnMetadataBuilder
    {
        public ColumnMetadataBuilder autoIncrement(int begin)
        {
            this.autoIncrement = new AutoIncrementMetadata(begin);
            return this;
        }
    }
}
