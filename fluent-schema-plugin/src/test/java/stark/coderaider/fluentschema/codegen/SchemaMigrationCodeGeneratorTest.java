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
        TableSchemaInfo tableSchemaInfoOfP1 = EntityParser.parse(P1.class);

        TableSchemaInfo tableSchemaInfoOfP2 = EntityParser.parse(P2.class);

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
        TableSchemaInfo tableSchemaInfoOfP1 = EntityParser.parse(P1.class);
        TableSchemaInfo tableSchemaInfoOfP2 = EntityParser.parse(P2.class);
        TableSchemaInfo tableSchemaInfoOfP3 = EntityParser.parse(Student.class);
        TableSchemaInfo tableSchemaInfoOfP4 = EntityParser.parse(Teacher.class);
        TableSchemaInfo tableSchemaInfoOfP5 = EntityParser.parse(TDrop.class);
        TableSchemaInfo tableSchemaInfoOfP6 = EntityParser.parse(TAdd.class);
        TableSchemaInfo tableSchemaInfoOfP7 = EntityParser.parse(TBeforeRenameColumn.class);
        TableSchemaInfo tableSchemaInfoOfP8 = EntityParser.parse(TAfterRenameColumn.class);
        TableSchemaInfo tableSchemaInfoOfP9 = EntityParser.parse(TBeforeAlterColumn.class);
        TableSchemaInfo tableSchemaInfoOfP10 = EntityParser.parse(TAfterAlterColumn.class);

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
        TableSchemaInfo tableSchemaInfoOfP1 = EntityParser.parse(P1.class);
        TableSchemaInfo tableSchemaInfoOfP2 = EntityParser.parse(P2.class);
        TableSchemaInfo tableSchemaInfoOfP3 = EntityParser.parse(Student.class);
        TableSchemaInfo tableSchemaInfoOfP4 = EntityParser.parse(Teacher.class);
        TableSchemaInfo tableSchemaInfoOfP5 = EntityParser.parse(TDrop.class);
        TableSchemaInfo tableSchemaInfoOfP6 = EntityParser.parse(TAdd.class);
        TableSchemaInfo tableSchemaInfoOfP7 = EntityParser.parse(TBeforeRenameColumn.class);
        TableSchemaInfo tableSchemaInfoOfP8 = EntityParser.parse(TAfterRenameColumn.class);
        TableSchemaInfo tableSchemaInfoOfP9 = EntityParser.parse(TBeforeAlterColumn.class);
        TableSchemaInfo tableSchemaInfoOfP10 = EntityParser.parse(TAfterAlterColumn.class);

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
        TableSchemaInfo tableSchemaInfo = EntityParser.parse(SchemaSnapshotHistory.class);

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