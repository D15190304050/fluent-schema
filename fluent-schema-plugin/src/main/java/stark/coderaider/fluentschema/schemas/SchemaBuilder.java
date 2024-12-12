package stark.coderaider.fluentschema.schemas;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class SchemaBuilder
{
    private final List<TableSchemaMetadata> tableSchemaMetadata;

    public SchemaBuilder()
    {
        tableSchemaMetadata = new ArrayList<>();
    }

    public void table(String name, Consumer<TableSchemaMetadataBuilder> consumer)
    {
        TableSchemaMetadataBuilder builder = new TableSchemaMetadataBuilder(name);
        consumer.accept(builder);
        TableSchemaMetadata tableSchemaMetadata = builder.toTableSchemaInfo();
        this.tableSchemaMetadata.add(tableSchemaMetadata);
    }
}
