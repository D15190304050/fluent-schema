package stark.coderaider.fluentschema.test.entities.migration;

import lombok.Data;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

/**
 * New teacher table, will be renamed from student.
 */
@Data
@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class Teacher
{
    @PrimaryKey
    private long id;
    private long schoolId;
    private String name;
}
