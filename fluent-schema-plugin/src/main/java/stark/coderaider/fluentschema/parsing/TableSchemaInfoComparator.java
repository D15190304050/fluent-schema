package stark.coderaider.fluentschema.parsing;

import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.parsing.differences.*;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The table {@link TableSchemaInfoComparator} class provides a method to compare and get the difference of the schemas of the same table in 2 snapshots.
 */
public final class TableSchemaInfoComparator
{
    private TableSchemaInfoComparator()
    {}

    public static TableSchemaDifference compareTableSchemaInfos(List<TableSchemaInfo> newTableSchemaInfos, List<TableSchemaInfo> oldTableSchemaInfos)
    {
        // Case 1: Rename table, all columns are identical.
        // Case 2: Change storage engine.
        // Case 3: Column level change.
        // Case 3.1: Rename column.
        // Case 3.2: Same column name, change column.
        // Case 4: Change keys.
        // Case 5: Change comment.
        // Case 6: Change primary key.

        if (newTableSchemaInfos == null || oldTableSchemaInfos == null)
            throw new NullPointerException("newTableSchemaInfos & oldTableSchemaInfos can not be null.");

        Map<String, TableSchemaInfo> newTableSchemaInfoMap = newTableSchemaInfos.stream().collect(Collectors.toMap(TableSchemaInfo::getName, Function.identity()));
        Map<String, TableSchemaInfo> oldTableSchemaInfoMap = oldTableSchemaInfos.stream().collect(Collectors.toMap(TableSchemaInfo::getName, Function.identity()));

        List<String> tablesWithNewName = new ArrayList<>();
        List<String> sameTableNames = new ArrayList<>();
        List<TableChangeDifference> tablesToAlter = new ArrayList<>();
        List<TableRenameDifference> tablesToRename = new ArrayList<>();

        // 1st pass, find columns that have or do not have the same name.
        for (String tableName : newTableSchemaInfoMap.keySet())
        {
            TableSchemaInfo newTableSchemaInfo = newTableSchemaInfoMap.get(tableName);
            TableSchemaInfo oldTableSchemaInfo = oldTableSchemaInfoMap.get(tableName);

            if (oldTableSchemaInfo == null)
                tablesWithNewName.add(tableName);
            else
            {
                sameTableNames.add(tableName);

                // If the program get into this branch, then newTableSchemaInfo & oldTableSchemaInfo have the same table name.
                // Then, if they are equal (edit distance = 0), then nothing is needed to do on this column.
                // Otherwise, we apply the alter table command.
                int editDistance = EditDistanceCalculator.getEditDistance(newTableSchemaInfo, oldTableSchemaInfo);
                if (editDistance != 0)
                {
                    TableChangeDifference tableChangeDifference = new TableChangeDifference();
                    tableChangeDifference.setName(tableName);
                    tableChangeDifference.setOldTableSchemaInfo(oldTableSchemaInfo);
                    tableChangeDifference.setNewTableSchemaInfo(newTableSchemaInfo);
                    tablesToAlter.add(tableChangeDifference);
                }
            }
        }

        // 2nd pass, remove tables with same names from 2 maps.
        // Remove these table names, so that we only need to take care about tables that can not match later.
        for (String tableName : sameTableNames)
        {
            oldTableSchemaInfoMap.remove(tableName);
            newTableSchemaInfoMap.remove(tableName);
        }

        // 3rd pass, find tables to rename.
        for (String newTableName : tablesWithNewName)
        {
            TableSchemaInfo newTableSchemaInfo = newTableSchemaInfoMap.get(newTableName);

            for (TableSchemaInfo oldTableSchemaInfo : oldTableSchemaInfoMap.values())
            {
                String oldTableName = oldTableSchemaInfo.getName();
                if (!containsOldTableName(tablesToRename, oldTableName))
                {
                    int editDistance = EditDistanceCalculator.getEditDistance(newTableSchemaInfo, oldTableSchemaInfo);
                    if (editDistance == 0)
                    {
                        TableRenameDifference tableRenameDifference = new TableRenameDifference();
                        tableRenameDifference.setOldName(oldTableName);
                        tableRenameDifference.setNewName(newTableName);
                        tablesToRename.add(tableRenameDifference);
                    }
                }
            }
        }

        // 4th pass, remove tables that need to be renamed from 2 maps.
        for (TableRenameDifference tableRenameDifference : tablesToRename)
        {
            String oldTableName = tableRenameDifference.getOldName();
            String newTableName = tableRenameDifference.getNewName();

            oldTableSchemaInfoMap.remove(oldTableName);
            newTableSchemaInfoMap.remove(newTableName);
        }

        // 5th pass, find tables to add / remove.
        // If there is any element in newTableSchemaInfoMap, then it is a new column.
        // And if there is any element in oldTableSchemaInfoMap, then it is a column to be removed.
        List<TableSchemaInfo> tablesToAdd = new ArrayList<>(newTableSchemaInfoMap.values());
        List<TableSchemaInfo> tablesToDrop = new ArrayList<>(oldTableSchemaInfoMap.values());

        TableSchemaDifference tableSchemaDifference = new TableSchemaDifference();
        tableSchemaDifference.setTablesToAdd(tablesToAdd);
        tableSchemaDifference.setTablesToDrop(tablesToDrop);
        tableSchemaDifference.setTablesToAlter(tablesToAlter);
        tableSchemaDifference.setTablesToRename(tablesToRename);
        return tableSchemaDifference;
    }

    public static ColumnMetadataDifference compareColumnMetadatas(List<ColumnMetadata> newColumnMetadatas, List<ColumnMetadata> oldColumnMetadatas)
    {
        Map<String, ColumnMetadata> newColumnMetadataMap = newColumnMetadatas.stream().collect(Collectors.toMap(ColumnMetadata::getName, Function.identity()));
        Map<String, ColumnMetadata> oldColumnMetadataMap = oldColumnMetadatas.stream().collect(Collectors.toMap(ColumnMetadata::getName, Function.identity()));

        List<String> columnsWithNewName = new ArrayList<>();
        List<String> sameColumnNames = new ArrayList<>();
        List<ColumnAlterDifference> columnsToAlter = new ArrayList<>();
        List<ColumnRenameDifference> columnsToRename = new ArrayList<>();

        // 1st pass, find columns that have or do not have the same name.
        for (String columnName : newColumnMetadataMap.keySet())
        {
            ColumnMetadata newColumnMetadata = newColumnMetadataMap.get(columnName);
            ColumnMetadata oldColumnMetadata = oldColumnMetadataMap.get(columnName);

            if (oldColumnMetadata == null)
                columnsWithNewName.add(columnName);
            else
            {
                sameColumnNames.add(columnName);

                // If the program get into this branch, then newColumnMetadata & oldColumnMetadata have the same column name.
                // Then, if they are equal (edit distance = 0), then nothing is needed to do on this column.
                // Otherwise, we apply the change column command.
                int editDistance = EditDistanceCalculator.getEditDistance(newColumnMetadata, oldColumnMetadata);
                if (editDistance != 0)
                {
                    ColumnAlterDifference columnAlterDifference = new ColumnAlterDifference();
                    columnAlterDifference.setName(columnName);
                    columnAlterDifference.setOldColumnMetadata(oldColumnMetadata);
                    columnAlterDifference.setNewColumnMetadata(newColumnMetadata);
                    columnsToAlter.add(columnAlterDifference);
                }
            }
        }

        // 2nd pass, remove columns with same names from 2 maps.
        // Remove these column names, so that we only need to take care about column that can not match later.
        for (String columnName : sameColumnNames)
        {
            oldColumnMetadataMap.remove(columnName);
            newColumnMetadataMap.remove(columnName);
        }

        // 3rd pass, find columns to rename.
        for (String newColumnName : columnsWithNewName)
        {
            ColumnMetadata newColumnMetadata = newColumnMetadataMap.get(newColumnName);

            for (ColumnMetadata oldColumnMetadata : oldColumnMetadataMap.values())
            {
                String oldColumnName = oldColumnMetadata.getName();
                if (!containsOldColumnName(columnsToRename, oldColumnName))
                {
                    int editDistance = EditDistanceCalculator.getEditDistance(newColumnMetadata, oldColumnMetadata);
                    if (editDistance == 0)
                    {
                        ColumnRenameDifference columnRenameDifference = new ColumnRenameDifference();
                        columnRenameDifference.setNewColumnMetadata(newColumnMetadata);
                        columnRenameDifference.setOldColumnMetadata(oldColumnMetadata);
                        columnsToRename.add(columnRenameDifference);
                    }
                }
            }
        }

        // 4th pass, remove columns that need to be renamed from 2 maps & columnsWithNewName.
        for (ColumnRenameDifference columnRenameDifference : columnsToRename)
        {
            String oldColumnName = columnRenameDifference.getOldColumnMetadata().getName();
            String newColumnName = columnRenameDifference.getNewColumnMetadata().getName();

//            columnsWithNewName.remove(newColumnName);
            oldColumnMetadataMap.remove(oldColumnName);
            newColumnMetadataMap.remove(newColumnName);
        }

        // 5th pass, find columns to add / remove.
        // If there is any element in newColumnMetadataMap, then it is a new column.
        // And if there is any element in oldColumnMetadataMap, then it is a column to be removed.
        List<ColumnMetadata> columnsToAdd = new ArrayList<>(newColumnMetadataMap.values());
        List<ColumnMetadata> columnsToDrop = new ArrayList<>(oldColumnMetadataMap.values());

        ColumnMetadataDifference columnMetadataDifference = new ColumnMetadataDifference();
        columnMetadataDifference.setColumnsToAlter(columnsToAlter);
        columnMetadataDifference.setColumnsToRename(columnsToRename);
        columnMetadataDifference.setColumnsToAdd(columnsToAdd);
        columnMetadataDifference.setColumnsToDrop(columnsToDrop);
        return columnMetadataDifference;
    }

    private static boolean containsOldColumnName(List<ColumnRenameDifference> columnsToRename, String oldColumnName)
    {
        for (ColumnRenameDifference columnChangeDifference : columnsToRename)
        {
            if (columnChangeDifference.getOldColumnMetadata().getName().equals(oldColumnName))
                return true;
        }
        return false;
    }

    private static boolean containsOldTableName(List<TableRenameDifference> tablesToRename, String oldTableName)
    {
        for (TableRenameDifference tableChangeDifference : tablesToRename)
        {
            if (tableChangeDifference.getOldName().equals(oldTableName))
                return true;
        }
        return false;
    }

    public static KeyMetadataDifference compareKeyMetadatas(List<KeyMetadata> newKeyMetadatas, List<KeyMetadata> oldKeyMetadatas)
    {
        Map<String, KeyMetadata> newKeyMetadataMap = newKeyMetadatas.stream().collect(Collectors.toMap(KeyMetadata::getName, Function.identity()));
        Map<String, KeyMetadata> oldKeyMetadataMap = oldKeyMetadatas.stream().collect(Collectors.toMap(KeyMetadata::getName, Function.identity()));

        Set<String> newKeyNames = newKeyMetadataMap.keySet();
        Set<String> oldKeyNames = oldKeyMetadataMap.keySet();

        HashSet<String> namesOfKeysToAdd = difference(newKeyNames, oldKeyNames);
        List<KeyMetadata> keysToAdd = new ArrayList<>();
        for (String keyName : namesOfKeysToAdd)
            keysToAdd.add(newKeyMetadataMap.get(keyName));

        HashSet<String> namesOfKeysToDrop = difference(oldKeyNames, newKeyNames);
        List<KeyMetadata> keysToDrop = new ArrayList<>();
        for (String keyName : namesOfKeysToDrop)
            keysToDrop.add(oldKeyMetadataMap.get(keyName));

        List<KeyAlterDifference> keysToAlter = new ArrayList<>();
        for (String newKeyName : newKeyNames)
        {
            if (!namesOfKeysToAdd.contains(newKeyName) && !namesOfKeysToDrop.contains(newKeyName))
            {
                KeyMetadata newKeyMetadata = newKeyMetadataMap.get(newKeyName);
                KeyMetadata oldKeyMetadata = oldKeyMetadataMap.get(newKeyName);
                if (!newKeyMetadata.equals(oldKeyMetadata))
                {
                    KeyAlterDifference keyAlterDifference = new KeyAlterDifference();
                    keyAlterDifference.setName(newKeyName);
                    keyAlterDifference.setOldKeyMetadata(oldKeyMetadata);
                    keyAlterDifference.setNewKeyMetadata(newKeyMetadata);
                    keysToAlter.add(keyAlterDifference);
                }
            }
        }

        KeyMetadataDifference keyMetadataDifference = new KeyMetadataDifference();
        keyMetadataDifference.setKeysToAlter(keysToAlter);
        keyMetadataDifference.setKeysToAdd(keysToAdd);
        keyMetadataDifference.setKeysToDrop(keysToDrop);
        return keyMetadataDifference;
    }

    private static <T> HashSet<T> difference(Set<T> set1, Set<T> set2)
    {
        HashSet<T> diff = new HashSet<>(set1);
        diff.removeAll(set2);
        return diff;
    }
}
