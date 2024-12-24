package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class DropKeyOperation extends MigrationOperationBase
{
    private String tableName;
    private String keyName;

    @Override
    public String toSql()
    {
        return MessageFormat.format(
            """
                ALTER TABLE `{0}` DROP KEY `{1}`;
                """,
            tableName,
            keyName
        ).trim();
    }
}
