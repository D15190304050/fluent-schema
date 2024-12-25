package stark.coderaider.fluentschema.test.migrations;

import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;

public class EmptyMigration extends SchemaMigrationBase
{
    @Override
    public void forward()
    {
        setInitialized(false);
    }

    @Override
    public void backward()
    {
    }
}