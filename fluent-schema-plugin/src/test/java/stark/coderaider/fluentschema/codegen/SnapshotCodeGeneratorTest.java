package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.junit.Test;
import stark.coderaider.fluentschema.parsing.EntityParser;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.test.entities.Person;

import static org.junit.Assert.*;

public class SnapshotCodeGeneratorTest
{

    @Test
    public void generateTableBuilderCode() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser = new EntityParser();
        TableSchemaInfo schemaInfoOfPerson = parser.parse(Person.class);

        SnapshotCodeGenerator generator = new SnapshotCodeGenerator();
        String s = generator.generateTableBuilderCode(schemaInfoOfPerson);
        System.out.println(s);
    }
}