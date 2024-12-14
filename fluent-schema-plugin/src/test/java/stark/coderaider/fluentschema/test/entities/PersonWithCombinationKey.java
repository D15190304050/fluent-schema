package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.*;

import java.util.Date;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class PersonWithCombinationKey
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Key(name = "idx_name")
    @Column(type = "VARCHAR(200)")
    private String name;

    @Key(name = "idx_name", order = 1)
    private String birthPlace;

    private String gender;

    private Date birthday;
}
