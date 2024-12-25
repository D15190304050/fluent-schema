package stark.coderaider.fluentschema.codegen;

import org.apache.maven.plugin.MojoExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.entities.SchemaSnapshotHistory;
import stark.coderaider.fluentschema.parsing.EntityParser;
import stark.coderaider.fluentschema.test.entities.migration.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SchemaMigrationCodeGeneratorTest
{
    @Test
    public void testGenerateSchemaMigration1() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser1 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP1 = parser1.parse(P1.class);

        EntityParser parser2 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP2 = parser2.parse(P2.class);

        String code = SchemaMigrationCodeGenerator.generateSchemaMigration(
            "stark.coderaider.fluentschema.examples",
            "SchemaMigration01",
            List.of(tableSchemaInfoOfP2),
            List.of(tableSchemaInfoOfP1),
            true
        );

        System.out.println(code);
    }

    @Test
    public void testGenerateSchemaMigration5() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser1 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP1 = parser1.parse(P1.class);

        EntityParser parser2 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP2 = parser2.parse(P2.class);

        EntityParser parser3 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP3 = parser3.parse(Student.class);

        EntityParser parser4 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP4 = parser4.parse(Teacher.class);

        EntityParser parser5 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP5 = parser5.parse(TDrop.class);

        EntityParser parser6 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP6 = parser6.parse(TAdd.class);

        EntityParser parser7 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP7 = parser7.parse(TBeforeRenameColumn.class);

        EntityParser parser8 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP8 = parser8.parse(TAfterRenameColumn.class);

        EntityParser parser9 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP9 = parser9.parse(TBeforeAlterColumn.class);

        EntityParser parser10 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP10 = parser10.parse(TAfterAlterColumn.class);

        List<TableSchemaInfo> oldTableSchemaInfos = List.of(tableSchemaInfoOfP1, tableSchemaInfoOfP3, tableSchemaInfoOfP5, tableSchemaInfoOfP7, tableSchemaInfoOfP9);
        List<TableSchemaInfo> newTableSchemaInfos = List.of(tableSchemaInfoOfP2, tableSchemaInfoOfP4, tableSchemaInfoOfP6, tableSchemaInfoOfP8, tableSchemaInfoOfP10);

        String code = SchemaMigrationCodeGenerator.generateSchemaMigration(
            "stark.coderaider.fluentschema.examples",
            "SchemaMigration05",
            newTableSchemaInfos,
            oldTableSchemaInfos,
            true);

        System.out.println(code);
    }

    @Test
    public void testGenerateSchemaMigration6() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser1 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP1 = parser1.parse(P1.class);

        EntityParser parser2 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP2 = parser2.parse(P2.class);

        EntityParser parser3 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP3 = parser3.parse(Student.class);

        EntityParser parser4 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP4 = parser4.parse(Teacher.class);

        EntityParser parser5 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP5 = parser5.parse(TDrop.class);

        EntityParser parser6 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP6 = parser6.parse(TAdd.class);

        EntityParser parser7 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP7 = parser7.parse(TBeforeRenameColumn.class);

        EntityParser parser8 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP8 = parser8.parse(TAfterRenameColumn.class);

        EntityParser parser9 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP9 = parser9.parse(TBeforeAlterColumn.class);

        EntityParser parser10 = new EntityParser();
        TableSchemaInfo tableSchemaInfoOfP10 = parser10.parse(TAfterAlterColumn.class);

        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = List.of(tableSchemaInfoOfP2, tableSchemaInfoOfP4, tableSchemaInfoOfP6, tableSchemaInfoOfP8, tableSchemaInfoOfP10);

        String code = SchemaMigrationCodeGenerator.generateSchemaMigration(
            "stark.coderaider.fluentschema.examples",
            "SchemaMigration06",
            newTableSchemaInfos,
            oldTableSchemaInfos,
            false);

        System.out.println(code);
    }

    @Test
    public void testGenerateSchemaMigration7() throws MojoExecutionException, BadLocationException
    {
        EntityParser parser = new EntityParser();
        TableSchemaInfo tableSchemaInfo = parser.parse(SchemaSnapshotHistory.class);

        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = List.of(tableSchemaInfo);

        String code = SchemaMigrationCodeGenerator.generateSchemaMigration(
            "stark.coderaider.fluentschema.examples",
            "SchemaMigration07",
            newTableSchemaInfos,
            oldTableSchemaInfos,
            false);

        System.out.println(code);
    }
}