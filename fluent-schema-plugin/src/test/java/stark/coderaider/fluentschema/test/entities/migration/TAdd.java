package stark.coderaider.fluentschema.test.entities.migration;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Key;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

import java.util.Date;

@Data
@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class TAdd
{
    @PrimaryKey
    private long id;

    private Date birthday;
}
