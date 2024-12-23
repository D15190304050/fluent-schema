package stark.coderaider.fluentschema.commons.schemas;

import stark.coderaider.fluentschema.commons.schemas.operations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SchemaMigrationBuilder
{
    private MigrationOperationInfo migrationOperationInfo;

    public SchemaMigrationBuilder()
    {
        migrationOperationInfo = new MigrationOperationInfo();
        migrationOperationInfo.setColumnsToAdd(new ArrayList<>());
        migrationOperationInfo.setColumnsToDrop(new ArrayList<>());
        migrationOperationInfo.setColumnsToRename(new ArrayList<>());
        migrationOperationInfo.setColumnsToAlter(new ArrayList<>());
        migrationOperationInfo.setTablesToDrop(new ArrayList<>());
        migrationOperationInfo.setTablesToAdd(new ArrayList<>());
        migrationOperationInfo.setTablesToRename(new ArrayList<>());
        migrationOperationInfo.setKeysToDrop(new ArrayList<>());
        migrationOperationInfo.setKeysToAdd(new ArrayList<>());
    }

    public void addColumn(String tableName, ColumnMetadata columnToAdd)
    {
        AddColumnOperation addColumnOperation = new AddColumnOperation();
        addColumnOperation.setTableName(tableName);
        addColumnOperation.setColumnMetadata(columnToAdd);
        migrationOperationInfo.getColumnsToAdd().add(addColumnOperation);
    }

    public void dropColumn(String tableName, String columnName)
    {
        DropColumnOperation dropColumnOperation = new DropColumnOperation();
        dropColumnOperation.setTableName(tableName);
        dropColumnOperation.setColumnName(columnName);
        migrationOperationInfo.getColumnsToDrop().add(dropColumnOperation);
    }

    public void renameColumn(String tableName, String oldColumnName, String newColumnName)
    {
        RenameColumnOperation renameColumnOperation = new RenameColumnOperation();
        renameColumnOperation.setTableName(tableName);
        renameColumnOperation.setOldColumnName(oldColumnName);
        renameColumnOperation.setNewColumnName(newColumnName);
        migrationOperationInfo.getColumnsToRename().add(renameColumnOperation);
    }

    public void alterColumn(String tableName, ColumnMetadata columnToAlter)
    {
        AlterColumnOperation alterColumnOperation = new AlterColumnOperation();
        alterColumnOperation.setTableName(tableName);
        alterColumnOperation.setColumnMetadata(columnToAlter);
        migrationOperationInfo.getColumnsToAlter().add(alterColumnOperation);
    }

    public void dropTable(String tableName)
    {
        migrationOperationInfo.getTablesToDrop().add(tableName);
    }

    public void addTable(String name, Consumer<TableSchemaBuilder> consumer)
    {
        TableSchemaInfo tableToAdd = TableSchemaBuilder.build(name, consumer);
        migrationOperationInfo.getTablesToAdd().add(tableToAdd);
    }

    public void addKey(String tableName, KeyMetadata keyToAdd)
    {
        AddKeyOperation addKeyOperation = new AddKeyOperation();
        addKeyOperation.setTableName(tableName);
        addKeyOperation.setKeyMetadata(keyToAdd);
        migrationOperationInfo.getKeysToAdd().add(addKeyOperation);
    }

    public void dropKey(String tableName, String keyName)
    {
        DropKeyOperation dropKeyOperation = new DropKeyOperation();
        dropKeyOperation.setTableName(tableName);
        dropKeyOperation.setKeyName(keyName);
        migrationOperationInfo.getKeysToDrop().add(dropKeyOperation);
    }

    public void renameTable(String oldTableName, String newTableName)
    {
        RenameTableOperation renameTableOperation = new RenameTableOperation();
        renameTableOperation.setOldTableName(oldTableName);
        renameTableOperation.setNewTableName(newTableName);
        migrationOperationInfo.getTablesToRename().add(renameTableOperation);
    }

    public MigrationOperationInfo toMigrationOperationInfo()
    {
        return migrationOperationInfo;
    }
}
