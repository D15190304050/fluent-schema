package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.AutoIncrementMetadata;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

import java.text.MessageFormat;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddColumnOperation extends MigrationOperationBase
{
    private String tableName;
    private ColumnMetadata columnMetadata;

    @Override
    public String toSql()
    {
        ColumnDefinition columnDefinition = new ColumnDefinition(columnMetadata);

        return MessageFormat.format(
                """
                    ALTER TABLE `{0}` ADD COLUMN {1}
                    """,
                tableName,
                columnDefinition.toSql()
            )
            .trim()
            + ";";
    }
}
