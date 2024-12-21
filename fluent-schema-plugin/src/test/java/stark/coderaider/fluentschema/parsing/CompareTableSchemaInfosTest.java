package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.parsing.differences.ColumnMetadataDifference;
import stark.coderaider.fluentschema.parsing.differences.EditDistanceCalculator;
import stark.coderaider.fluentschema.parsing.differences.TableChangeDifference;
import stark.coderaider.fluentschema.parsing.differences.TableSchemaDifference;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.test.entities.Person;
import stark.coderaider.fluentschema.test.entities.PersonWithCombinationKey;
import stark.dataworks.basic.data.json.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class CompareTableSchemaInfosTest
{
    /**
     * Case 1: No change.
     * */
    @Test
    public void testCompareTableSchemaInfos1() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        newTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertTrue(tableSchemaDifference.noChange());
    }

    /**
     * Case 2: Rename.
     */
    @Test
    public void testCompareTableSchemaInfos2() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        TableSchemaInfo schemaInfoOfPerson = entityParser.parse(Person.class);
        oldTableSchemaInfos.add(schemaInfoOfPerson);
        newTableSchemaInfos.add(schemaInfoOfPerson);

        TableSchemaInfo schemaInfoOfPerson2 = entityParser.parse(PersonWithCombinationKey.class);
        oldTableSchemaInfos.add(schemaInfoOfPerson2);
        TableSchemaInfo schemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey.class);
        schemaInfoOfPerson3.setName("PersonWithCombinationKey");
        newTableSchemaInfos.add(schemaInfoOfPerson3);

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        String tableSchemaDifferenceJson = JsonSerializer.serialize(tableSchemaDifference);
        System.out.println(tableSchemaDifferenceJson);
        TableSchemaDifference correct = JsonSerializer.deserialize("{\"tablesToChange\":[],\"tablesToRename\":[{\"oldName\":\"person_with_combination_key\",\"newName\":\"PersonWithCombinationKey\"}],\"tablesToAdd\":[],\"tablesToRemove\":[]}", TableSchemaDifference.class);
        Assert.assertEquals(tableSchemaDifference, correct);
    }

    /**
     * Case 3: Change comment.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos3() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        TableSchemaInfo tableSchemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey.class);
        tableSchemaInfoOfPerson3.setComment("Comment of PersonWithCombinationKey.");
        newTableSchemaInfos.add(tableSchemaInfoOfPerson3);

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        List<TableChangeDifference> tablesToChange = tableSchemaDifference.getTablesToChange();
        TableChangeDifference tableToChange = tablesToChange.get(0);
        TableSchemaInfo oldTableSchemaInfo = tableToChange.getOldTableSchemaInfo();
        TableSchemaInfo newTableSchemaInfo = tableToChange.getNewTableSchemaInfo();

        List<ColumnMetadata> oldColumnMetadatas = oldTableSchemaInfo.getColumnMetadatas();
        List<ColumnMetadata> newColumnMetadatas = newTableSchemaInfo.getColumnMetadatas();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        Assert.assertTrue(columnMetadataDifference.noChange());

        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getName));
        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getEngine));

        Assert.assertEquals(newTableSchemaInfo.getPrimaryKeyMetadata(), oldTableSchemaInfo.getPrimaryKeyMetadata());
        Assert.assertEquals(newTableSchemaInfo.getKeyMetadatas(), oldTableSchemaInfo.getKeyMetadatas());

        Assert.assertEquals(1, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getComment));
    }

    /**
     * Case 4: Change comment & engine.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos4() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        TableSchemaInfo tableSchemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey.class);
        tableSchemaInfoOfPerson3.setComment("Comment of PersonWithCombinationKey.");
        tableSchemaInfoOfPerson3.setEngine("MyISAM");
        newTableSchemaInfos.add(tableSchemaInfoOfPerson3);

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        List<TableChangeDifference> tablesToChange = tableSchemaDifference.getTablesToChange();
        TableChangeDifference tableToChange = tablesToChange.get(0);
        TableSchemaInfo oldTableSchemaInfo = tableToChange.getOldTableSchemaInfo();
        TableSchemaInfo newTableSchemaInfo = tableToChange.getNewTableSchemaInfo();

        List<ColumnMetadata> oldColumnMetadatas = oldTableSchemaInfo.getColumnMetadatas();
        List<ColumnMetadata> newColumnMetadatas = newTableSchemaInfo.getColumnMetadatas();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        Assert.assertTrue(columnMetadataDifference.noChange());

        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getName));

        Assert.assertEquals(newTableSchemaInfo.getPrimaryKeyMetadata(), oldTableSchemaInfo.getPrimaryKeyMetadata());
        Assert.assertEquals(newTableSchemaInfo.getKeyMetadatas(), oldTableSchemaInfo.getKeyMetadatas());

        Assert.assertEquals(1, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getComment));
        Assert.assertEquals(1, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getEngine));
    }
}
