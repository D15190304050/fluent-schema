package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;

import java.util.List;

@Data
public class ColumnMetadataDifference
{
    List<ColumnChangeDifference> columnsToChange;
    List<ColumnRenameDifference> columnsToRename;
    List<ColumnMetadata> columnsToAdd;
    List<ColumnMetadata> columnsToRemove;

    /**
     * Returns {@code true} if there is no change on column metadata; otherwise, {@code false}.
     * @return {@code true} if there is no change on column metadata; otherwise, {@code false}.
     */
    public boolean noChange()
    {
        return CollectionUtils.isEmpty(columnsToChange) &&
            CollectionUtils.isEmpty(columnsToRename) &&
            CollectionUtils.isEmpty(columnsToAdd) &&
            CollectionUtils.isEmpty(columnsToRemove);
    }
}
