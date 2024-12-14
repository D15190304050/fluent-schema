package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

import java.util.Date;

@Table(comment = "Table of basic information of persons.")
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
