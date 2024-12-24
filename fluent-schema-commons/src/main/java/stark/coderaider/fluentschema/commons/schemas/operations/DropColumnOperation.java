package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class DropColumnOperation extends MigrationOperationBase
{
    private String tableName;
    private String columnName;

    @Override
    public String toSql()
    {
        return MessageFormat.format(
            """
                ALTER TABLE `{0}` DROP COLUMN `{1}`;
                """,
            tableName,
            columnName
        ).trim();
    }
}
