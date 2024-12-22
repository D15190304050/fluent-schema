package stark.coderaider.fluentschema.commons.schemas;

import stark.coderaider.fluentschema.commons.schemas.operations.MigrationOperationInfo;

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

    public MigrationOperationInfo toForwardOperationInfo()
    {
        return forwardBuilder.toMigrationOperationInfo();
    }

    public MigrationOperationInfo toBackwardOperationInfo()
    {
        return backwardBuilder.toMigrationOperationInfo();
    }
}
