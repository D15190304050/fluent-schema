package stark.coderaider.fluentschema.commons.schemas;

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

    public void table(String name, Consumer<TableSchemaBuilder> consumer)
    {
        TableSchemaInfo tableSchemaInfo =  TableSchemaBuilder.build(name, consumer);
        this.tableSchemaInfos.add(tableSchemaInfo);
    }
}
