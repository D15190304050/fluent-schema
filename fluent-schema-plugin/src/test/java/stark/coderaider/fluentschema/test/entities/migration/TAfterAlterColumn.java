package stark.coderaider.fluentschema.test.entities.migration;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Key;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Data
@Table(name = "t_alter_column_type", namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class TAfterAlterColumn
{
    @PrimaryKey
    private long id;

    @Key(name = "idx_name")
    @Column(type = "VARCHAR(100)")
    private String name;
}
