package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;
import org.springframework.util.CollectionUtils;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

import java.util.List;

@Data
public class TableSchemaDifference
{
    List<TableChangeDifference> tablesToAlter;
    List<TableRenameDifference> tablesToRename;
    List<TableSchemaInfo> tablesToAdd;
    List<TableSchemaInfo> tablesToDrop;

    /**
     * Returns {@code true} if there is no change on table schema; otherwise, {@code false}.
     * @return {@code true} if there is no change on table schema; otherwise, {@code false}.
     */
    public boolean noChange()
    {
        return CollectionUtils.isEmpty(tablesToAlter) &&
            CollectionUtils.isEmpty(tablesToRename) &&
            CollectionUtils.isEmpty(tablesToAdd) &&
            CollectionUtils.isEmpty(tablesToDrop);
    }
}
