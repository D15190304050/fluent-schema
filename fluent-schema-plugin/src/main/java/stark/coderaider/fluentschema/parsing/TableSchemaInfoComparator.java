package stark.coderaider.fluentschema.parsing;

import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.parsing.differences.ColumnChangeDifference;
import stark.coderaider.fluentschema.parsing.differences.ColumnRenameDifference;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The table {@link TableSchemaInfoComparator} class provides a method to compare and get the difference of the schemas of the same table in 2 snapshots.
 */
public final class TableSchemaInfoComparator
{
    private TableSchemaInfoComparator()
    {}

    public void compareTableSchemaInfo(TableSchemaInfo left, TableSchemaInfo right)
    {
        // Case 1: Rename table, all columns are identical.
        // Case 2: Change storage engine.
        // Case 3: Column level change.
        // Case 3.1: Rename column.
        // Case 3.2: Same column name, change column.
        // Case 4: Change keys.
        // Case 5: Change comment.
        // Case 6: Change primary key.

        if (left == null || right == null)
            throw new NullPointerException("Left & right can not be null.");
    }

    public void compareColumnMetadatas(List<ColumnMetadata> newColumnMetadatas, List<ColumnMetadata> oldColumnMetadatas)
    {
        Map<String, ColumnMetadata> newColumnMetadataMap = newColumnMetadatas.stream().collect(Collectors.toMap(ColumnMetadata::getName, x -> x));
        Map<String, ColumnMetadata> oldColumnMetadataMap = oldColumnMetadatas.stream().collect(Collectors.toMap(ColumnMetadata::getName, x -> x));

        List<String> columnsNoMapping = new ArrayList<>();
        List<ColumnMetadata> columnsToAdd = new ArrayList<>();
        List<ColumnMetadata> columnsToRemove = new ArrayList<>();
        List<ColumnChangeDifference> columnsToChange = new ArrayList<>();
        List<ColumnRenameDifference> columnsToRename = new ArrayList<>();

        // 1st pass, find columns that
        for (String columnName : newColumnMetadataMap.keySet())
        {
            ColumnMetadata newColumnMetadata = newColumnMetadataMap.get(columnName);

            ColumnMetadata oldColumnMetadata = oldColumnMetadataMap.get(columnName);
            if (oldColumnMetadata == null)
                columnsNoMapping.add(columnName);
            else
            {
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
    }
}
