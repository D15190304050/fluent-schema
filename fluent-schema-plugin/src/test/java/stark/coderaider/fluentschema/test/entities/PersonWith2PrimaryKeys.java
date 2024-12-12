package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.annotations.AutoIncrement;
import stark.coderaider.fluentschema.annotations.Column;
import stark.coderaider.fluentschema.annotations.PrimaryKey;

import java.util.Date;

public class PersonWith2PrimaryKeys
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Column(type = "VARCHAR(200)")
    private String name;

    @PrimaryKey
    private String gender;

    private Date birthday;
}
