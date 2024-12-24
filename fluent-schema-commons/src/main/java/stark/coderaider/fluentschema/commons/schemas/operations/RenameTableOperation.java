package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class RenameTableOperation extends MigrationOperationBase
{
    private String oldTableName;
    private String newTableName;

    @Override
    public String toSql()
    {
        return MessageFormat.format(
            """
                RENAME TABLE `{0}` TO `{1}`;
                """,
            oldTableName,
            newTableName
        ).trim();
    }
}
