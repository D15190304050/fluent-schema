package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;

@Data
public class ColumnRenameDifference
{
    private String oldName;
    private String newName;
}
