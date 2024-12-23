package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;

@Data
public abstract class MigrationOperationBase
{
    private long order;
    private String type;

    public abstract String toSql();

    public String getType()
    {
        return getClass().getSimpleName();
    }
}
