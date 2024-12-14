package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.annotations.*;

import java.util.Date;

@Table(comment = "Persons, with 2 keys.")
public class PersonWith2Keys
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Key(name = "idx_name")
    @Column(type = "VARCHAR(200)")
    private String name;

    @Key(name = "idx_birth_place")
    private String birthPlace;

    private String gender;

    private Date birthday;
}
