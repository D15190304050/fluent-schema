package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import stark.coderaider.fluentschema.schemas.TableSchemaMetadata;
import stark.coderaider.fluentschema.test.entities.Person;
import stark.coderaider.fluentschema.test.entities.PersonWith2AutoIncrements;
import stark.coderaider.fluentschema.test.entities.PersonWith2PrimaryKeys;
import stark.dataworks.basic.data.json.JsonSerializer;

import static org.junit.Assert.*;

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
}