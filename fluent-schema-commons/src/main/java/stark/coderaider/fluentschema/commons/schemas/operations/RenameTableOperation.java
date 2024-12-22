package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.Data;

@Data
public class RenameTableOperation
{
    private String oldTableName;
    private String newTableName;
}
