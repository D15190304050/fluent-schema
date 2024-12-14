package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

import java.util.Date;

@Table(comment = "Person, auto increment with error column constraint.")
public class PersonAutoIncrementWithErrorColumnConstraint
{
    @PrimaryKey
    private long id;

    @Column(type = "VARCHAR(200)")
    private String name;

    @AutoIncrement
    private int something;

    private String gender;

    private Date birthday;
}
