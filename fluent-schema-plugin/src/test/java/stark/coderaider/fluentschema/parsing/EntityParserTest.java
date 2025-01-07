package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.entities.SchemaSnapshotHistory;
import stark.coderaider.fluentschema.test.entities.*;
import stark.dataworks.basic.data.json.JsonSerializer;

public class EntityParserTest
{

    @Test
    public void parse() throws MojoExecutionException
    {
        TableSchemaInfo tableSchemaInfo = EntityParser.parse(Person.class);

        System.out.println(JsonSerializer.serialize(tableSchemaInfo));
    }

    @Test
    public void parse2PrimaryKeys()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(PersonWith2PrimaryKeys.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parse2AutoIncrements()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(PersonWith2AutoIncrements.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parseAutoIncrementOnErrorColumnType()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(PersonAutoIncrementOnErrorColumnType.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parseAutoIncrementWithErrorColumnConstraint()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(PersonAutoIncrementWithErrorColumnConstraint.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parsePersonWith2Keys()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(PersonWith2Keys.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parsePersonWithCombinationKey()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(PersonWithCombinationKey.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void parseSchemaSnapshotHistory()
    {
        try
        {
            TableSchemaInfo tableSchemaInfo = EntityParser.parse(SchemaSnapshotHistory.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }
}