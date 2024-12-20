package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.parsing.differences.*;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The table {@link TableSchemaInfoComparator} class provides a method to compare and get the difference of the schemas of the same table in 2 snapshots.
 */
public final class TableSchemaInfoComparator
{
    private TableSchemaInfoComparator()
    {}

    public void compareTableSchemaInfos(List<TableSchemaInfo> newTableSchemaInfos, List<TableSchemaInfo> oldTableSchemaInfos)
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
        List<TableSchemaInfo> tablesToAlter = new ArrayList<>();
        List<TableSchemaInfo> tablesToRename = new ArrayList<>();

        // 1st pass, find columns that have or do not have the same name.
    }

    public static ColumnMetadataDifference compareColumnMetadatas(List<ColumnMetadata> newColumnMetadatas, List<ColumnMetadata> oldColumnMetadatas)
    {
        Map<String, ColumnMetadata> newColumnMetadataMap = newColumnMetadatas.stream().collect(Collectors.toMap(ColumnMetadata::getName, Function.identity()));
        Map<String, ColumnMetadata> oldColumnMetadataMap = oldColumnMetadatas.stream().collect(Collectors.toMap(ColumnMetadata::getName, Function.identity()));

        List<String> columnsWithNewName = new ArrayList<>();
        List<String> sameColumnNames = new ArrayList<>();
        List<ColumnChangeDifference> columnsToChange = new ArrayList<>();
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
                // Then, if they are equal, then nothing is needed to do on this column.
                // Otherwise, we apply the change column command.
                if (!newColumnMetadata.equals(oldColumnMetadata))
                {
                    ColumnChangeDifference columnChangeDifference = new ColumnChangeDifference();
                    columnChangeDifference.setName(columnName);
                    columnChangeDifference.setOldColumnMetadata(oldColumnMetadata);
                    columnChangeDifference.setNewColumnMetadata(newColumnMetadata);
                    columnsToChange.add(columnChangeDifference);
                }
            }
        }

        // 2nd pass, remove columns with same names from 2 maps.
        for (String columnName : sameColumnNames)
        {
            // Remove these column names, so that we only need to take care about column that can not match.
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
                        columnRenameDifference.setNewName(newColumnName);
                        columnRenameDifference.setOldName(oldColumnName);
                        columnsToRename.add(columnRenameDifference);
                    }
                }
            }
        }

        // 4th pass, remove columns that need to be renamed from 2 maps & columnsWithNewName.
        for (ColumnRenameDifference columnRenameDifference : columnsToRename)
        {
            String oldColumnName = columnRenameDifference.getOldName();
            String newColumnName = columnRenameDifference.getNewName();

            columnsWithNewName.remove(newColumnName);
            oldColumnMetadataMap.remove(oldColumnName);
            newColumnMetadataMap.remove(newColumnName);
        }

        // 5th pass, find columns to add / remove.
        // If there is any element in newColumnMetadataMap, then it is a new column.
        // And if there is any element in oldColumnMetadataMap, then it is a column to be removed.
        // And they must have same count.
        List<ColumnMetadata> columnsToAdd = new ArrayList<>(newColumnMetadataMap.values());
        List<ColumnMetadata> columnsToRemove = new ArrayList<>(oldColumnMetadataMap.values());

        ColumnMetadataDifference columnMetadataDifference = new ColumnMetadataDifference();
        columnMetadataDifference.setColumnsToChange(columnsToChange);
        columnMetadataDifference.setColumnsToRename(columnsToRename);
        columnMetadataDifference.setColumnsToAdd(columnsToAdd);
        columnMetadataDifference.setColumnsToRemove(columnsToRemove);
        return columnMetadataDifference;
    }

    private static boolean containsOldColumnName(List<ColumnRenameDifference> columnsToRename, String oldColumnName)
    {
        for (ColumnRenameDifference columnChangeDifference : columnsToRename)
        {
            if (columnChangeDifference.getOldName().equals(oldColumnName))
                return true;
        }
        return false;
    }
}
