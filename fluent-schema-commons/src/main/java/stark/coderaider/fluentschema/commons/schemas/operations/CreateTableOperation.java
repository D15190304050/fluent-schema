package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

import java.text.MessageFormat;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateTableOperation extends MigrationOperationBase
{
    private TableSchemaInfo tableSchemaInfo;

    @Override
    public String toSql()
    {
        List<ColumnMetadata> columnMetadatas = tableSchemaInfo.getColumnMetadatas();
        StringBuilder columnDefinitionsBuilder = new StringBuilder();
        for (ColumnMetadata columnMetadata : columnMetadatas)
        {
            ColumnDefinition columnDefinition = new ColumnDefinition(columnMetadata);
            columnDefinitionsBuilder
                .append("    ")
                .append(columnDefinition.toSql().trim())
                .append(",")
                .append(System.lineSeparator());
        }

        List<KeyMetadata> keyMetadatas = tableSchemaInfo.getKeyMetadatas();
        StringBuilder keyDefinitionsBuilder = new StringBuilder();
        for (KeyMetadata keyMetadata : keyMetadatas)
        {
            KeyDefinition keyDefinition = new KeyDefinition(keyMetadata);
            keyDefinitionsBuilder
                .append(System.lineSeparator())
                .append("    ")
                .append(keyDefinition.toSql().trim())
                .append(",");
        }

        String comment = tableSchemaInfo.getComment();
        CommentBuilder commentBuilder = new CommentBuilder(comment);
        String commentSql = commentBuilder.toSql();
        if (!commentSql.isEmpty())
            commentSql = System.lineSeparator() + "    " + commentSql;

        return MessageFormat.format(
            """
                CREATE TABLE `{0}`
                (
                {1}
                    PRIMARY KEY (`{2}`){3}
                )
                    ENGINE = {4}{5};
                """,
            tableSchemaInfo.getName(),
            columnDefinitionsBuilder.toString(),
            tableSchemaInfo.getPrimaryKeyMetadata().getColumnName(),
            keyDefinitionsBuilder.isEmpty()
                ? ""
                : keyDefinitionsBuilder.substring(0, keyDefinitionsBuilder.length() - 1),
            tableSchemaInfo.getEngine(),
            commentSql
        ).trim();
    }
}
