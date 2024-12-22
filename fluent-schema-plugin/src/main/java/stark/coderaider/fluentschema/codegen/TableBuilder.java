package stark.coderaider.fluentschema.codegen;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import stark.coderaider.fluentschema.commons.schemas.*;

import java.util.List;

public final class TableBuilder
{
    private TableBuilder()
    {
    }

    public static void build(StringBuilder tableBuilder, TableSchemaInfo tableSchemaInfo)
    {
        // Columns.
        List<ColumnMetadata> columnMetadatas = tableSchemaInfo.getColumnMetadatas();
        for (ColumnMetadata columnMetadata : columnMetadatas)
            appendColumnBuilder(tableBuilder, columnMetadata);

        // Primary key.
        PrimaryKeyMetadata primaryKeyMetadata = tableSchemaInfo.getPrimaryKeyMetadata();
        if (primaryKeyMetadata != null)
        {
            tableBuilder
                .append("builder.primaryKey()")
                .append(".columnName(\"")
                .append(primaryKeyMetadata.getColumnName())
                .append("\");");
        }

        // Keys (Indexes).
        List<KeyMetadata> keyMetadatas = tableSchemaInfo.getKeyMetadatas();
        if (!CollectionUtils.isEmpty(keyMetadatas))
        {
            for (KeyMetadata keyMetadata : keyMetadatas)
                appendKeyBuilder(tableBuilder, keyMetadata);
        }

        // Engine.
        tableBuilder
            .append("builder.engine(\"")
            .append(tableSchemaInfo.getEngine())
            .append("\");");

        // Comment.
        String comment = tableSchemaInfo.getComment();
        if (StringUtils.hasText(comment))
        {
            tableBuilder
                .append("builder.comment(\"")
                .append(comment)
                .append("\");");
        }
    }

    private static void appendKeyBuilder(StringBuilder tableBuilder, KeyMetadata keyMetadata)
    {
        tableBuilder.append("builder.key()");

        // Key name.
        tableBuilder
            .append(".name(\"")
            .append(keyMetadata.getName())
            .append("\")");

        // Key columns.
        String joinedColumnNames = String.join("\", \"", keyMetadata.getColumns());
        tableBuilder
            .append(".columns(List.of(\"")
            .append(joinedColumnNames)
            .append("\"));");
    }

    private static void appendColumnBuilder(StringBuilder tableBuilder, ColumnMetadata columnMetadata)
    {
        tableBuilder.append("builder.column()");

        // Column name.
        tableBuilder
            .append(".name(\"")
            .append(columnMetadata.getName())
            .append("\")");

        // Column data type.
        tableBuilder
            .append(".type(\"")
            .append(columnMetadata.getType())
            .append("\")");

        // Nullable.
        tableBuilder
            .append(".nullable(")
            .append(columnMetadata.isNullable())
            .append(")");

        // Unique.
        tableBuilder
            .append(".unique(")
            .append(columnMetadata.isUnique())
            .append(")");

        // Comment.
        String comment = columnMetadata.getComment();
        if (StringUtils.hasText(comment))
        {
            tableBuilder
                .append(".comment(\"")
                .append(comment)
                .append("\")");
        }

        // Default value.
        String defaultValue = columnMetadata.getDefaultValue();
        if (StringUtils.hasText(defaultValue))
        {
            tableBuilder
                .append(".defaultValue(\"")
                .append(defaultValue)
                .append("\")");
        }

        // On update.
        String onUpdate = columnMetadata.getOnUpdate();
        if (StringUtils.hasText(onUpdate))
        {
            tableBuilder
                .append(".onUpdate(\"")
                .append(onUpdate)
                .append("\")");
        }

        // Auto increment.
        AutoIncrementMetadata autoIncrement = columnMetadata.getAutoIncrement();
        if (autoIncrement != null)
        {
            tableBuilder
                .append(".autoIncrement(")
                .append(autoIncrement.getBegin())
                .append(", ")
                .append(autoIncrement.getIncrement())
                .append(")");
        }

        tableBuilder.append(";");
    }
}
