package stark.coderaider.fluentschema.test.entities.migration;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Key;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

import java.util.Date;

@Table(name = "person", namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
@Data
public class P2
{
    @PrimaryKey
    private long id;

    @Key(name = "idx_birth_date")
    private Date birthDate;
}
