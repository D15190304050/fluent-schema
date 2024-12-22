package stark.coderaider.fluentschema.codegen;

import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.parsing.TableSchemaInfoComparator;
import stark.coderaider.fluentschema.parsing.differences.TableChangeDifference;
import stark.coderaider.fluentschema.parsing.differences.TableRenameDifference;
import stark.coderaider.fluentschema.parsing.differences.TableSchemaDifference;

import java.util.List;

public class SchemaMigrationCodeGenerator
{
    public static String generateSchemaMigration(String packageName, String className, List<TableSchemaInfo> newTableSchemaInfos, List<TableSchemaInfo> oldTableSchemaInfos)
    {
        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);

        StringBuilder schemaMigrationBuilder = new StringBuilder();

        // Package statement.
        schemaMigrationBuilder
            .append("package ")
            .append(packageName)
            .append(";");

        // Import statement.
        schemaMigrationBuilder
            .append("import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;")
            .append("import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;");

        // Class name.
        schemaMigrationBuilder
            .append("public class ")
            .append(className)
            .append(" extends SchemaMigrationBase")
            .append(" {");

        // Forward.

        // Backward.

        // End of class.
        schemaMigrationBuilder.append("}");

        return schemaMigrationBuilder.toString();
    }

    public static void buildForwardMethod(StringBuilder schemaMigrationBuilder, TableSchemaDifference tableSchemaDifference)
    {
        // Method declaration.
        schemaMigrationBuilder
            .append("@Override")
            .append("public void forward()")
            .append("{");

        // Tables to add.
        List<TableSchemaInfo> tablesToAdd = tableSchemaDifference.getTablesToAdd();
        for (TableSchemaInfo tableSchemaInfo : tablesToAdd)
        {
            schemaMigrationBuilder
                .append("forwardBuilder.addTable(\"")
                .append(tableSchemaInfo.getName())
                .append("\", builder ->{");

            TableBuilder.build(schemaMigrationBuilder, tableSchemaInfo);

            schemaMigrationBuilder
                .append("});");
        }

        // Tables to drop.
        List<TableSchemaInfo> tablesToDrop = tableSchemaDifference.getTablesToDrop();
        for (TableSchemaInfo tableSchemaInfo : tablesToDrop)
        {
            schemaMigrationBuilder
                .append("forwardBuilder.dropTable(\"")
                .append(tableSchemaInfo.getName())
                .append("\");");
        }

        // Tables to rename.
        List<TableRenameDifference> tablesToRename = tableSchemaDifference.getTablesToRename();
        for (TableRenameDifference tableRenameDifference : tablesToRename)
        {
            schemaMigrationBuilder
                .append("forwardBuilder.renameTable(\"")
                .append(tableRenameDifference.getOldName())
                .append("\"")
                .append(", ")
                .append("\"")
                .append(tableRenameDifference.getNewName())
                .append("\");");
        }

        // Tables to alter.
        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        for (TableChangeDifference tableChangeDifference : tablesToAlter)
        {
            TableSchemaInfo oldTableSchemaInfo = tableChangeDifference.getOldTableSchemaInfo();
            TableSchemaInfo newTableSchemaInfo = tableChangeDifference.getNewTableSchemaInfo();


        }

        // Method end.
        schemaMigrationBuilder.append("}");
    }
}
