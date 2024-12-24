package stark.coderaider.fluentschema.commons.schemas.operations;

import lombok.AllArgsConstructor;
import lombok.Data;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;

import java.text.MessageFormat;

@Data
@AllArgsConstructor
public class KeyDefinition
{
    private KeyMetadata keyMetadata;

    public String toSql()
    {
        String joinedColumnNames = String.join("`, `", keyMetadata.getColumns());

        return MessageFormat.format(
                """
                    `{0}` (`{1}`)
                    """,
                keyMetadata.getName(),
                joinedColumnNames)
            .trim();
    }
}
