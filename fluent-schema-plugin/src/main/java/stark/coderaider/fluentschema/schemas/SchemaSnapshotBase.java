package stark.coderaider.fluentschema.schemas;

public abstract class SchemaSnapshotBase
{
    protected SchemaBuilder schemaBuilder;

    public SchemaSnapshotBase()
    {
        schemaBuilder = new SchemaBuilder();
    }

    public abstract void buildSchema();
}
