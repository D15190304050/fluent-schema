package stark.coderaider.fluentschema.commons.schemas;

import java.util.List;

public abstract class SchemaSnapshotBase
{
    protected SchemaBuilder schemaBuilder;

    public SchemaSnapshotBase()
    {
        schemaBuilder = new SchemaBuilder();
    }

    public abstract void buildSchema();

    public List<TableSchemaInfo> getTableSchemaInfos()
    {
        return schemaBuilder.getTableSchemaInfos();
    }
}
