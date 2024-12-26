package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jface.text.BadLocationException;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

import java.util.List;

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

        return CodeFormatter.formatCode(schemaSnapshotBuilder.toString());
    }

    public static void generateTableBuilderCode(StringBuilder tableBuilder, TableSchemaInfo tableSchemaInfo)
    {
        // Table name.
        tableBuilder
            .append("schemaBuilder.table(\"")
            .append(tableSchemaInfo.getName())
            .append("\", builder -> {");

        TableBuilder.build(tableBuilder, tableSchemaInfo);

        tableBuilder
            .append("});");

        tableBuilder.append(System.lineSeparator());
    }
}
