package stark.coderaider.fluentschema.test.entities.migration;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Key;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Data
@Table(name = "t_alter_column", namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class TBeforeRenameColumn
{
    @PrimaryKey
    private long id;

    @Key(name = "idx_name")
    private String name;
}
