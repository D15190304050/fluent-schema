package stark.coderaider.fluentschema.codegen;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.parsing.TableSchemaInfoComparator;
import stark.coderaider.fluentschema.parsing.differences.*;

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
            .append("import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;")
            .append("import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;")
            .append("import java.util.List;");

        // Class name.
        schemaMigrationBuilder
            .append("public class ")
            .append(className)
            .append(" extends SchemaMigrationBase")
            .append(" {");

        // Forward.
        buildForwardMethod(schemaMigrationBuilder, tableSchemaDifference);

        // Backward.
        buildBackwardMethod(schemaMigrationBuilder, tableSchemaDifference);

        // End of class.
        schemaMigrationBuilder.append("}");

        return schemaMigrationBuilder.toString();
    }

    public static void buildForwardMethod(StringBuilder schemaMigrationBuilder, TableSchemaDifference tableSchemaDifference)
    {
        // Method declaration.
        schemaMigrationBuilder
            .append("@Override")
            .append(" ")
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
        // Orders:
        // 1. Drop old keys.
        // 2. Alter columns.
        // 3. Add new keys.
        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        for (TableChangeDifference tableChangeDifference : tablesToAlter)
        {
            String tableName = tableChangeDifference.getName();
            TableSchemaInfo oldTableSchemaInfo = tableChangeDifference.getOldTableSchemaInfo();
            TableSchemaInfo newTableSchemaInfo = tableChangeDifference.getNewTableSchemaInfo();

            KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(newTableSchemaInfo.getKeyMetadatas(), oldTableSchemaInfo.getKeyMetadatas());
            List<KeyMetadata> keysToDrop = keyMetadataDifference.getKeysToDrop();
            List<KeyMetadata> keysToAdd = keyMetadataDifference.getKeysToAdd();
            List<KeyAlterDifference> keysToAlter = keyMetadataDifference.getKeysToAlter();

            ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newTableSchemaInfo.getColumnMetadatas(), oldTableSchemaInfo.getColumnMetadatas());
            List<ColumnMetadata> columnsToDrop = columnMetadataDifference.getColumnsToDrop();
            List<ColumnMetadata> columnsToAdd = columnMetadataDifference.getColumnsToAdd();
            List<ColumnAlterDifference> columnsToAlter = columnMetadataDifference.getColumnsToAlter();
            List<ColumnRenameDifference> columnsToRename = columnMetadataDifference.getColumnsToRename();

            for (KeyMetadata keyToDrop : keysToDrop)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.dropKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(keyToDrop.getName())
                    .append("\");");
            }

            for (KeyAlterDifference keyToAlter : keysToAlter)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.dropKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(keyToAlter.getName())
                    .append("\");");
            }

            for (ColumnMetadata columnToDrop : columnsToDrop)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.dropColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(columnToDrop.getName())
                    .append("\");");
            }

            for (ColumnMetadata columnToAdd : columnsToAdd)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.addColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("ColumnMetadata.builder()");

                TableBuilder.appendColumnBuilderBody(schemaMigrationBuilder, columnToAdd);

                schemaMigrationBuilder
                    .append(".build()")
                    .append(");");
            }

            for (ColumnAlterDifference columnToAlter : columnsToAlter)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.alterColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("ColumnMetadata.builder()");

                TableBuilder.appendColumnBuilderBody(schemaMigrationBuilder, columnToAlter.getNewColumnMetadata());

                schemaMigrationBuilder
                    .append(".build()")
                    .append(");");
            }

            for (ColumnRenameDifference columnToRename : columnsToRename)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.renameColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(columnToRename.getOldName())
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(columnToRename.getNewName())
                    .append("\");");
            }

            for (KeyAlterDifference keyToAlter : keysToAlter)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.addKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("KeyMetadata.builder()");

                TableBuilder.appendKeyBuilderBody(schemaMigrationBuilder, keyToAlter.getNewKeyMetadata());

                schemaMigrationBuilder
                    .append(".build());");
            }

            for (KeyMetadata keyToAdd : keysToAdd)
            {
                schemaMigrationBuilder
                    .append("forwardBuilder.addKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("KeyMetadata.builder()");

                TableBuilder.appendKeyBuilderBody(schemaMigrationBuilder, keyToAdd);

                schemaMigrationBuilder
                    .append(".build());");
            }
        }

        // Method end.
        schemaMigrationBuilder.append("}");
    }

    // backward() must have a completely opposite (reversed) direction of forward().
    public static void buildBackwardMethod(StringBuilder schemaMigrationBuilder, TableSchemaDifference tableSchemaDifference)
    {
        // Method declaration.
        schemaMigrationBuilder
            .append("@Override")
            .append(" ")
            .append("public void backward()")
            .append("{");

        // Tables to alter.
        // Orders:
        // 1. Drop new keys.
        // 2. Alter columns.
        // 3. Add old keys.
        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        for (TableChangeDifference tableChangeDifference : tablesToAlter)
        {
            // TODO: optimize the duplicate here.
            String tableName = tableChangeDifference.getName();
            TableSchemaInfo oldTableSchemaInfo = tableChangeDifference.getOldTableSchemaInfo();
            TableSchemaInfo newTableSchemaInfo = tableChangeDifference.getNewTableSchemaInfo();

            KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(newTableSchemaInfo.getKeyMetadatas(), oldTableSchemaInfo.getKeyMetadatas());
            List<KeyMetadata> keysToDrop = keyMetadataDifference.getKeysToDrop();
            List<KeyMetadata> keysToAdd = keyMetadataDifference.getKeysToAdd();
            List<KeyAlterDifference> keysToAlter = keyMetadataDifference.getKeysToAlter();

            ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newTableSchemaInfo.getColumnMetadatas(), oldTableSchemaInfo.getColumnMetadatas());
            List<ColumnMetadata> columnsToDrop = columnMetadataDifference.getColumnsToDrop();
            List<ColumnMetadata> columnsToAdd = columnMetadataDifference.getColumnsToAdd();
            List<ColumnAlterDifference> columnsToAlter = columnMetadataDifference.getColumnsToAlter();
            List<ColumnRenameDifference> columnsToRename = columnMetadataDifference.getColumnsToRename();

            for (KeyMetadata keyToAdd : keysToAdd)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.dropKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(keyToAdd.getName())
                    .append("\");");
            }

            for (KeyAlterDifference keyToAlter : keysToAlter)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.dropKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(keyToAlter.getName())
                    .append("\");");
            }

            for (ColumnRenameDifference columnToRename : columnsToRename)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.renameColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(columnToRename.getNewName())
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(columnToRename.getOldName())
                    .append("\");");
            }

            for (ColumnAlterDifference columnToAlter : columnsToAlter)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.alterColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("ColumnMetadata.builder()");

                TableBuilder.appendColumnBuilderBody(schemaMigrationBuilder, columnToAlter.getOldColumnMetadata());

                schemaMigrationBuilder
                    .append(".build()")
                    .append(");");
            }

            for (ColumnMetadata columnToAdd : columnsToAdd)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.dropColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("\"")
                    .append(columnToAdd.getName())
                    .append("\");");
            }

            for (ColumnMetadata columnToDrop : columnsToDrop)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.addColumn(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("ColumnMetadata.builder()");

                TableBuilder.appendColumnBuilderBody(schemaMigrationBuilder, columnToDrop);

                schemaMigrationBuilder
                    .append(".build()")
                    .append(");");
            }

            for (KeyAlterDifference keyToAlter : keysToAlter)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.addKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("KeyMetadata.builder()");

                TableBuilder.appendKeyBuilderBody(schemaMigrationBuilder, keyToAlter.getOldKeyMetadata());

                schemaMigrationBuilder
                    .append(".build());");
            }

            for (KeyMetadata keyToDrop : keysToDrop)
            {
                schemaMigrationBuilder
                    .append("backwardBuilder.addKey(\"")
                    .append(tableName)
                    .append("\"")
                    .append(", ")
                    .append("KeyMetadata.builder()");

                TableBuilder.appendKeyBuilderBody(schemaMigrationBuilder, keyToDrop);

                schemaMigrationBuilder
                    .append(".build());");
            }
        }

        // Method end.
        schemaMigrationBuilder.append("}");
    }
}
