package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.AllArgsConstructor;
import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.AutoIncrementMetadata;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

import java.text.MessageFormat;

@Data
@AllArgsConstructor
public class ColumnDefinition
{
    private ColumnMetadata columnMetadata;

    public String toSql()
    {
        String defaultValue = columnMetadata.getDefaultValue();
        String onUpdate = columnMetadata.getOnUpdate();
        AutoIncrementMetadata autoIncrement = columnMetadata.getAutoIncrement();
        String comment = columnMetadata.getComment();
        CommentBuilder commentBuilder = new CommentBuilder(comment);

        return MessageFormat.format(
                """
                    `{0}` {1} {2} {3} {4} {5} {6} {7}
                    """,
                columnMetadata.getName(),
                columnMetadata.getType(),
                columnMetadata.isNullable() ? "NULL" : "NOT NULL",
                columnMetadata.isUnique() ? "UNIQUE" : "",
                defaultValue == null ? "" : "DEFAULT " + defaultValue,
                onUpdate == null ? "" : "ON UPDATE " + onUpdate,
                autoIncrement == null ? "" : "AUTO_INCREMENT",
                commentBuilder.toSql())
            .trim();
    }
}
