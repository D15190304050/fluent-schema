package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import stark.coderaider.fluentschema.commons.schemas.AutoIncrementMetadata;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.PrimaryKeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnapshotCodeGenerator
{
    public static String generateSchemaSnapshot(String packageName, String className, List<TableSchemaInfo> tableSchemaInfos) throws MojoExecutionException, BadLocationException
    {
        StringBuilder schemaSnapshotBuilder = new StringBuilder();

        // Package statement.
        schemaSnapshotBuilder
            .append("package ")
            .append(packageName)
            .append(";");

        // Import statement.
        schemaSnapshotBuilder
            .append("import stark.coderaider.fluentschema.commons.schemas.SchemaSnapshotBase;")
            .append("import java.util.List;");

        // Class name.
        schemaSnapshotBuilder
            .append("public class ")
            .append(className)
            .append(" extends SchemaSnapshotBase")
            .append("{");

        // The buildSchema() method.
        schemaSnapshotBuilder
            .append("@Override")
            .append(System.lineSeparator())
            .append("public void buildSchema()")
            .append("{");

        for (TableSchemaInfo tableSchemaInfo : tableSchemaInfos)
            generateTableBuilderCode(schemaSnapshotBuilder, tableSchemaInfo);

        schemaSnapshotBuilder.append("}");

        // End of class.
        schemaSnapshotBuilder.append("}");

        return schemaSnapshotBuilder.toString();
//        return formatCode(schemaSnapshotBuilder.toString());
    }

    public static void generateTableBuilderCode(StringBuilder tableBuilder, TableSchemaInfo tableSchemaInfo)
    {
        // Table name.
        tableBuilder
            .append("schemaBuilder.table(\"")
            .append(tableSchemaInfo.getName())
            .append("\", builder -> {");

        // Columns.
        List<ColumnMetadata> columnMetadatas = tableSchemaInfo.getColumnMetadatas();
        for (ColumnMetadata columnMetadata : columnMetadatas)
            appendColumnBuilder(columnMetadata, tableBuilder);

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
                appendKeyBuilder(keyMetadata, tableBuilder);
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

        tableBuilder
            .append("});");

        tableBuilder.append(System.lineSeparator());
    }

    private static void appendKeyBuilder(KeyMetadata keyMetadata, StringBuilder tableBuilder)
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

    private static void appendColumnBuilder(ColumnMetadata columnMetadata, StringBuilder tableBuilder)
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

    public static String formatCode(String code) throws BadLocationException, MojoExecutionException
    {
        Map<String, String> options = new HashMap<>();
        options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_PACKAGE, "1");
        options.put(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AFTER_IMPORTS, "1");
        options.put("org.eclipse.jdt.core.formatter.tabulation.char", "space"); // 使用空格替代Tab
        options.put("org.eclipse.jdt.core.formatter.tabulation.size", "4");

        CodeFormatter formatter = ToolFactory.createCodeFormatter(options);
        TextEdit edit = formatter.format(CodeFormatter.K_COMPILATION_UNIT, code, 0, code.length(), 0, null);

        if (edit != null)
        {
            Document document = new Document(code);
            edit.apply(document);
            return document.get();
        }

        // Normally, this line of code is never executed.
        throw new MojoExecutionException("Unable to format code.");
    }
}
