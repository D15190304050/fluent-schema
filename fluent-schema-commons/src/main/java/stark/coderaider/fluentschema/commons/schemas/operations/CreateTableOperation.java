package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;
import lombok.EqualsAndHashCode;
import stark.coderaider.fluentschema.commons.schemas.AutoIncrementMetadata;
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
                .append(",")
                .append(System.lineSeparator())
                .append("    ")
                .append("KEY")
                .append(keyDefinition.toSql().trim());
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
                    ENGINE = {4}{5}{6};
                """,
            tableSchemaInfo.getName(),
            columnDefinitionsBuilder.toString(),
            tableSchemaInfo.getPrimaryKeyMetadata().getColumnName(),
            keyDefinitionsBuilder,
            tableSchemaInfo.getEngine(),
            getAutoIncrement(),
            commentSql
        ).trim();
    }

    private String getAutoIncrement()
    {
        List<ColumnMetadata> columnsWithAutoIncrement = tableSchemaInfo.getColumnMetadatas().stream().filter(x -> x.getAutoIncrement() != null).toList();
        if (!columnsWithAutoIncrement.isEmpty())
        {
            ColumnMetadata columnMetadata = columnsWithAutoIncrement.get(0);
            AutoIncrementMetadata autoIncrement = columnMetadata.getAutoIncrement();
            if (autoIncrement != null)
                return System.lineSeparator() + "    " + "AUTO_INCREMENT = " + autoIncrement.getBegin();
        }

        return "";
    }
}
