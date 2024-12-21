package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.test.entities.*;
import stark.dataworks.basic.data.json.JsonSerializer;

public class EntityParserTest
{

    @Test
    public void parse() throws MojoExecutionException
    {
        EntityParser parser = new EntityParser();
        TableSchemaInfo tableSchemaInfo = parser.parse(Person.class);

        System.out.println(JsonSerializer.serialize(tableSchemaInfo));
    }

    @Test
    public void parse2PrimaryKeys()
    {
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaInfo tableSchemaInfo = parser.parse(PersonWith2PrimaryKeys.class);
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
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaInfo tableSchemaInfo = parser.parse(PersonWith2AutoIncrements.class);
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
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaInfo tableSchemaInfo = parser.parse(PersonAutoIncrementOnErrorColumnType.class);
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
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaInfo tableSchemaInfo = parser.parse(PersonAutoIncrementWithErrorColumnConstraint.class);
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
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaInfo tableSchemaInfo = parser.parse(PersonWith2Keys.class);
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
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaInfo tableSchemaInfo = parser.parse(PersonWithCombinationKey.class);
            System.out.println(JsonSerializer.serialize(tableSchemaInfo));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }
}