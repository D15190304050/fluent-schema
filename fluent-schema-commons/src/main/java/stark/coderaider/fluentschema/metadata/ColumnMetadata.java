package stark.coderaider.fluentschema.metadata;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ColumnMetadata
{
    private String name;
    private String type;
    private boolean nullable;
    private String comment;
    private String defaultValue;
    private String onUpdate;
    private AutoIncrementMetadata autoIncrement;

    public static class ColumnMetadataBuilder
    {
        public ColumnMetadataBuilder autoIncrement(int begin, int increment)
        {
            this.autoIncrement = new AutoIncrementMetadata(begin, increment);
            return this;
        }
    }
}
