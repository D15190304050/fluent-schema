package stark.coderaider.fluentschema.commons.schemas;

import lombok.Getter;
import lombok.Setter;
import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationBase;

import java.util.List;

public abstract class SchemaMigrationBase
{
    protected SchemaMigrationBuilder forwardBuilder;
    protected SchemaMigrationBuilder backwardBuilder;

    @Getter
    @Setter
    protected boolean initialized;

    public SchemaMigrationBase()
    {
        forwardBuilder = new SchemaMigrationBuilder();
        backwardBuilder = new SchemaMigrationBuilder();
        initialized = true;
    }

    public abstract void forward();

    public abstract void backward();

    public void resetForwardOperations()
    {
        forwardBuilder.getMigrationOperations().clear();
    }

    public void resetBackwardOperations()
    {
        backwardBuilder.getMigrationOperations().clear();
    }

    public List<MigrationOperationBase> toForwardOperations()
    {
        return forwardBuilder.getMigrationOperations();
    }

    public List<MigrationOperationBase> toBackwardOperations()
    {
        return backwardBuilder.getMigrationOperations();
    }
}
