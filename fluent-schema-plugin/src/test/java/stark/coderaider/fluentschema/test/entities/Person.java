package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.annotations.*;

import java.util.Date;

@Table(comment = "Table of basic information of persons.")
public class Person
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Key(name = "idx_name")
    @Column(type = "VARCHAR(200)")
    private String name;

    private String gender;

    private Date birthday;
}
