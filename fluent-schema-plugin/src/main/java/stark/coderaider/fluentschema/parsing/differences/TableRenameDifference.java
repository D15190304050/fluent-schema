package stark.coderaider.fluentschema.parsing.differences;

import lombok.Data;

@Data
public class TableRenameDifference
{
    private String oldName;
    private String newName;
}
