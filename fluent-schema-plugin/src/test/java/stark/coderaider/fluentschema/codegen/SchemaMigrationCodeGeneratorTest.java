package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.parsing.EntityParser;
import stark.coderaider.fluentschema.test.entities.migration.P1;
import stark.coderaider.fluentschema.test.entities.migration.P2;

import java.util.List;

import static org.junit.Assert.*;

public class SchemaMigrationCodeGeneratorTest
{
    @Test
    public void testGenerateSchemaMigration() throws MojoExecutionException
    {
        EntityParser parser1 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP1 = parser1.parse(P1.class);

        EntityParser parser2 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP2 = parser2.parse(P2.class);

        String code = SchemaMigrationCodeGenerator.generateSchemaMigration(
            "stark.coderaider.fluentschema.examples",
            "SchemaMigration01",
            List.of(tableSchemaInfoOfP2),
            List.of(tableSchemaInfoOfP1)
        );

        System.out.println(code);
    }
}