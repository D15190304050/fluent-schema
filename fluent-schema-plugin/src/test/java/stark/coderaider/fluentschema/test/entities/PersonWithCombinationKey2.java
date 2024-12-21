package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.*;

import java.util.Date;

@Table(name = "person_with_combination_key", namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE)
public class PersonWithCombinationKey2
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Key(name = "idx_name")
    @Column(type = "VARCHAR(100)")
    private String name;

    @Key(name = "idx_name", order = 1)
    private String birthPlace;

    private Date birthdate;

    private int age;

}
