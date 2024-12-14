package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import stark.coderaider.fluentschema.schemas.TableSchemaMetadata;
import stark.coderaider.fluentschema.test.entities.*;
import stark.dataworks.basic.data.json.JsonSerializer;

public class EntityParserTest
{

    @Test
    public void parse() throws MojoExecutionException
    {
        EntityParser parser = new EntityParser();
        TableSchemaMetadata tableSchemaMetadata = parser.parse(Person.class);

        System.out.println(JsonSerializer.serialize(tableSchemaMetadata));
    }

    @Test
    public void parse2PrimaryKeys()
    {
        EntityParser parser = new EntityParser();

        try
        {
            TableSchemaMetadata tableSchemaMetadata = parser.parse(PersonWith2PrimaryKeys.class);
            System.out.println(JsonSerializer.serialize(tableSchemaMetadata));
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
            TableSchemaMetadata tableSchemaMetadata = parser.parse(PersonWith2AutoIncrements.class);
            System.out.println(JsonSerializer.serialize(tableSchemaMetadata));
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
            TableSchemaMetadata tableSchemaMetadata = parser.parse(PersonAutoIncrementOnErrorColumnType.class);
            System.out.println(JsonSerializer.serialize(tableSchemaMetadata));
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
            TableSchemaMetadata tableSchemaMetadata = parser.parse(PersonAutoIncrementWithErrorColumnConstraint.class);
            System.out.println(JsonSerializer.serialize(tableSchemaMetadata));
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
    }
}