package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddKeyOperation extends MigrationOperationBase
{
    private String tableName;
    private KeyMetadata keyMetadata;

    @Override
    public String toSql()
    {
        KeyDefinition keyDefinition = new KeyDefinition(keyMetadata);

        return MessageFormat.format(
                """
                    ALTER TABLE `{0}` ADD KEY {1}
                    """,
                tableName,
                keyDefinition.toSql())
            .trim()
            + ";";
    }
}
