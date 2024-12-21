package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.junit.Test;
import stark.coderaider.fluentschema.parsing.EntityParser;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.test.entities.Person;
import stark.coderaider.fluentschema.test.entities.PersonWithCombinationKey;

import java.util.List;

public class SnapshotCodeGeneratorTest
{
    @Test
    public void generateTableBuilderCode() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser = new EntityParser();
        TableSchemaInfo schemaInfoOfPerson = parser.parse(Person.class);

        StringBuilder tableSchemaInfoBuilder = new StringBuilder();
        SnapshotCodeGenerator.generateTableBuilderCode(tableSchemaInfoBuilder, schemaInfoOfPerson);
        System.out.println(tableSchemaInfoBuilder);
    }

    @Test
    public void testGenerateSchemaSnapshot() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser = new EntityParser();
        TableSchemaInfo schemaInfoOfPerson = parser.parse(Person.class);
        TableSchemaInfo schemaInfoOfPerson2 = parser.parse(PersonWithCombinationKey.class);

        String s = SnapshotCodeGenerator.generateSchemaSnapshot(
            "stark.coderaider.fluentschema.examples",
            "SchemaSnapshotExample2",
            List.of(schemaInfoOfPerson, schemaInfoOfPerson2));
        System.out.println(s);
    }
}