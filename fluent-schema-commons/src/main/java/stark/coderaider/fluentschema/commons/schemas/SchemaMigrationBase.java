package stark.coderaider.fluentschema.commons.schemas;

import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationBase;

import java.util.List;

public abstract class SchemaMigrationBase
{
    protected SchemaMigrationBuilder forwardBuilder;
    protected SchemaMigrationBuilder backwardBuilder;

    public SchemaMigrationBase()
    {
        forwardBuilder = new SchemaMigrationBuilder();
        backwardBuilder = new SchemaMigrationBuilder();
    }

    public abstract void forward();

    public abstract void backward();

    public List<MigrationOperationBase> toForwardOperations()
    {
        return forwardBuilder.getMigrationOperations();
    }

    public List<MigrationOperationBase> toBackwardOperations()
    {
        return backwardBuilder.getMigrationOperations();
    }
}
