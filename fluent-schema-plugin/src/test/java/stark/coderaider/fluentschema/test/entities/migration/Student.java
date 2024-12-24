package stark.coderaider.fluentschema.test.entities.migration;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

/**
 * Old student table, will be renamed to teacher.
 */
@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
@Data
public class Student
{
    @PrimaryKey
    private long id;
    private long schoolId;
    private String name;
}
