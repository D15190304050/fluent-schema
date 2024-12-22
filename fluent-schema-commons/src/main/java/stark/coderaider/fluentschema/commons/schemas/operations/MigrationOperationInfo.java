package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.AllArgsConstructor;
import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;

import java.util.List;

@Data
public class MigrationOperationInfo
{
    private List<AddColumnOperation> columnsToAdd;
    private List<DropColumnOperation> columnsToDrop;
    private List<RenameColumnOperation> columnsToRename;
    private List<AlterColumnOperation> columnsToAlter;
    private List<String> tablesToDrop;
    private List<TableSchemaInfo> tablesToAdd;
}
