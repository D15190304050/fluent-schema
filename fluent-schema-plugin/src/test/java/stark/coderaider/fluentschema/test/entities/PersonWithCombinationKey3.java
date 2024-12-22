package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.*;

import java.util.Date;

@Table(name = "person_with_combination_key", namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class PersonWithCombinationKey3
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Column(type = "VARCHAR(200)")
    private String name;

    @Key(name = "idx_name")
    private String gender;

    private Date birthday;
}
