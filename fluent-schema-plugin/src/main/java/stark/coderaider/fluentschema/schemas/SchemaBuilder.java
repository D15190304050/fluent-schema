package stark.coderaider.fluentschema.schemas;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class SchemaBuilder
{
    private final List<TableSchemaInfo> tableSchemaInfos;

    public SchemaBuilder()
    {
        tableSchemaInfos = new ArrayList<>();
    }

    public void table(String name, Consumer<TableSchemaMetadataBuilder> consumer)
    {
        TableSchemaMetadataBuilder builder = new TableSchemaMetadataBuilder(name);
        consumer.accept(builder);
        TableSchemaInfo tableSchemaInfo = builder.toTableSchemaInfo();
        tableSchemaInfos.add(tableSchemaInfo);
    }
}
