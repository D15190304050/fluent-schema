package stark.coderaider.fluentschema.test.entities;

import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

import java.util.Date;

@Table
public class PersonAutoIncrementOnErrorColumnType
{
    @PrimaryKey
    private long id;

    @Column(type = "VARCHAR(200)")
    @AutoIncrement
    private String name;

    private String gender;

    private Date birthday;
}
