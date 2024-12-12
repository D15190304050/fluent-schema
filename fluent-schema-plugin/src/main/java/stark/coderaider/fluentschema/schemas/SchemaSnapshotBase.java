package stark.coderaider.fluentschema.schemas;

import java.util.List;

public abstract class SchemaSnapshotBase
{
    protected SchemaBuilder schemaBuilder;

    public SchemaSnapshotBase()
    {
        schemaBuilder = new SchemaBuilder();
    }

    public abstract void buildSchema();

    public List<TableSchemaMetadata> getTableSchemaInfos()
    {
        return schemaBuilder.getTableSchemaMetadata();
    }
}
