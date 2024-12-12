package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.annotations.AutoIncrement;
import stark.coderaider.fluentschema.annotations.Column;
import stark.coderaider.fluentschema.annotations.PrimaryKey;
import stark.coderaider.fluentschema.annotations.Table;

import java.util.Date;

@Table
public class Person
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Column(type = "VARCHAR(200)")
    private String name;

    private String gender;

    private Date birthday;
}
