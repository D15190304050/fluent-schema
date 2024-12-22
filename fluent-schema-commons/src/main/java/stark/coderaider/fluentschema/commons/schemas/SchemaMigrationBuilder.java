package stark.coderaider.fluentschema.commons.schemas;

import stark.coderaider.fluentschema.commons.schemas.operations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SchemaMigrationBuilder
{
    private final List<AddColumnOperation> columnsToAdd;
    private final List<DropColumnOperation> columnsToDrop;
    private final List<RenameColumnOperation> columnsToRename;
    private final List<AlterColumnOperation> columnsToAlter;
    private final List<String> tablesToDrop;
    private final List<TableSchemaInfo> tablesToAdd;
    private final List<RenameTableOperation> tablesToRename;

    public SchemaMigrationBuilder()
    {
        columnsToAdd = new ArrayList<>();
        columnsToDrop = new ArrayList<>();
        columnsToRename = new ArrayList<>();
        columnsToAlter = new ArrayList<>();
        tablesToDrop = new ArrayList<>();
        tablesToAdd = new ArrayList<>();
        tablesToRename = new ArrayList<>();
    }

    public void addColumn(String tableName, ColumnMetadata columnToAdd)
    {
        AddColumnOperation addColumnOperation = new AddColumnOperation();
        addColumnOperation.setTableName(tableName);
        addColumnOperation.setColumnMetadata(columnToAdd);
        columnsToAdd.add(addColumnOperation);
    }

    public void dropColumn(String tableName, String columnName)
    {
        DropColumnOperation dropColumnOperation = new DropColumnOperation();
        dropColumnOperation.setTableName(tableName);
        dropColumnOperation.setColumnName(columnName);
        columnsToDrop.add(dropColumnOperation);
    }

    public void renameColumn(String tableName, String oldColumnName, String newColumnName)
    {
        RenameColumnOperation renameColumnOperation = new RenameColumnOperation();
        renameColumnOperation.setTableName(tableName);
        renameColumnOperation.setOldColumnName(oldColumnName);
        renameColumnOperation.setNewColumnName(newColumnName);
        columnsToRename.add(renameColumnOperation);
    }

    public void alterColumn(String table, ColumnMetadata columnToAlter)
    {
        AlterColumnOperation alterColumnOperation = new AlterColumnOperation();
        alterColumnOperation.setTableName(table);
        alterColumnOperation.setColumnMetadata(columnToAlter);
        columnsToAlter.add(alterColumnOperation);
    }

    public void dropTable(String tableName)
    {
        tablesToDrop.add(tableName);
    }

    public void addTable(String name, Consumer<TableSchemaBuilder> consumer)
    {
        TableSchemaInfo tableToAdd = TableSchemaBuilder.build(name, consumer);
        tablesToAdd.add(tableToAdd);
    }

    public void renameTable(String oldTableName, String newTableName)
    {
        RenameTableOperation renameTableOperation = new RenameTableOperation();
        renameTableOperation.setOldTableName(oldTableName);
        renameTableOperation.setNewTableName(newTableName);
        tablesToRename.add(renameTableOperation);
    }

    public MigrationOperationInfo toMigrationOperationInfo()
    {
        MigrationOperationInfo migrationOperationInfo = new MigrationOperationInfo();
        migrationOperationInfo.setTablesToAdd(tablesToAdd);
        migrationOperationInfo.setTablesToDrop(tablesToDrop);
        migrationOperationInfo.setColumnsToAdd(columnsToAdd);
        migrationOperationInfo.setColumnsToDrop(columnsToDrop);
        migrationOperationInfo.setColumnsToRename(columnsToRename);
        migrationOperationInfo.setColumnsToAlter(columnsToAlter);
        migrationOperationInfo.setTablesToRename(tablesToRename);
        return migrationOperationInfo;
    }
}
