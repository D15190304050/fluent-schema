package stark.coderaider.fluentschema.commons.schemas;

import lombok.Getter;
import stark.coderaider.fluentschema.commons.schemas.operations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class SchemaMigrationBuilder
{
    private final List<MigrationOperationBase> migrationOperations;

    public SchemaMigrationBuilder()
    {
        migrationOperations = new ArrayList<>();
    }

    public void addColumn(String tableName, ColumnMetadata columnToAdd)
    {
        AddColumnOperation addColumnOperation = new AddColumnOperation();
        addColumnOperation.setTableName(tableName);
        addColumnOperation.setColumnMetadata(columnToAdd);
        migrationOperations.add(addColumnOperation);
    }

    public void dropColumn(String tableName, String columnName)
    {
        DropColumnOperation dropColumnOperation = new DropColumnOperation();
        dropColumnOperation.setTableName(tableName);
        dropColumnOperation.setColumnName(columnName);
        migrationOperations.add(dropColumnOperation);
    }

    public void alterColumn(String tableName, String oldColumnName, ColumnMetadata columnToAlter)
    {
        AlterColumnOperation alterColumnOperation = new AlterColumnOperation();
        alterColumnOperation.setTableName(tableName);
        alterColumnOperation.setOldColumnName(oldColumnName);
        alterColumnOperation.setNewColumnMetadata(columnToAlter);
        migrationOperations.add(alterColumnOperation);
    }

    public void dropTable(String tableName)
    {
        DropTableOperation dropTableOperation = new DropTableOperation();
        dropTableOperation.setTableName(tableName);
        migrationOperations.add(dropTableOperation);
    }

    public void addTable(String name, Consumer<TableSchemaBuilder> consumer)
    {
        TableSchemaInfo tableToAdd = TableSchemaBuilder.build(name, consumer);
        CreateTableOperation createTableOperation = new CreateTableOperation();
        createTableOperation.setTableSchemaInfo(tableToAdd);
        migrationOperations.add(createTableOperation);
    }

    public void addKey(String tableName, KeyMetadata keyToAdd)
    {
        AddKeyOperation addKeyOperation = new AddKeyOperation();
        addKeyOperation.setTableName(tableName);
        addKeyOperation.setKeyMetadata(keyToAdd);
        migrationOperations.add(addKeyOperation);
    }

    public void dropKey(String tableName, String keyName)
    {
        DropKeyOperation dropKeyOperation = new DropKeyOperation();
        dropKeyOperation.setTableName(tableName);
        dropKeyOperation.setKeyName(keyName);
        migrationOperations.add(dropKeyOperation);
    }

    public void renameTable(String oldTableName, String newTableName)
    {
        RenameTableOperation renameTableOperation = new RenameTableOperation();
        renameTableOperation.setOldTableName(oldTableName);
        renameTableOperation.setNewTableName(newTableName);
        migrationOperations.add(renameTableOperation);
    }
}
