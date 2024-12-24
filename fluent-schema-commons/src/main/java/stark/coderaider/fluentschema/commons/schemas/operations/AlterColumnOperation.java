package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlterColumnOperation extends MigrationOperationBase
{
    private String tableName;
    private String oldColumnName;
    private ColumnMetadata newColumnMetadata;

    @Override
    public String toSql()
    {
        ColumnDefinition columnDefinition = new ColumnDefinition(newColumnMetadata);

        return MessageFormat.format(
            """
                ALTER TABLE `{0}` CHANGE COLUMN `{1}` {2};
                """,
            tableName,
            oldColumnName,
            columnDefinition.toSql()
        ).trim();
    }
}
