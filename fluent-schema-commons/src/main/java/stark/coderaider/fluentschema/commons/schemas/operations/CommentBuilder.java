package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentBuilder
{
    private String comment;

    public String toSql()
    {
        return comment != null && !comment.isEmpty() ? "COMMENT '" + comment + "'" : "";
    }
}
