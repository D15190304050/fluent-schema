package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.parsing.differences.*;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.test.entities.Person;
import stark.coderaider.fluentschema.test.entities.PersonWith2Keys;
import stark.coderaider.fluentschema.test.entities.PersonWithCombinationKey;
import stark.coderaider.fluentschema.test.entities.PersonWithCombinationKey2;
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

    /**
     * Case 5: Add 1 table.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos5() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        newTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToRemove().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToChange().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToAdd().size());
    }

    /**
     * Case 6: Add 2 tables.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos6() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        newTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        newTableSchemaInfos.add(entityParser.parse(PersonWith2Keys.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToRemove().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToChange().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(2, tableSchemaDifference.getTablesToAdd().size());
    }

    /**
     * Case 7: Remove 1 table.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos7() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToAdd().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToChange().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToRemove().size());
    }

    /**
     * Case 8: Remove 2 tables.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos8() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        oldTableSchemaInfos.add(entityParser.parse(PersonWith2Keys.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToAdd().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToChange().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(2, tableSchemaDifference.getTablesToRemove().size());
    }

    /**
     * Case 9: Add 1 table, remove 1 table.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos9() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        newTableSchemaInfos.add(entityParser.parse(PersonWith2Keys.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToChange().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToAdd().size());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToRemove().size());
    }

    /**
     * Case 10: Add 1 table, rename 1 table.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos10() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        TableSchemaInfo schemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey.class);
        schemaInfoOfPerson3.setName("Person3");
        newTableSchemaInfos.add(schemaInfoOfPerson3);
        newTableSchemaInfos.add(entityParser.parse(PersonWith2Keys.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToChange().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRemove().isEmpty());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToAdd().size());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToRename().size());
    }

    /**
     * Case 11: Add 1 column to 1 table.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos11() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        TableSchemaInfo schemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey.class);
        schemaInfoOfPerson3.getColumnMetadatas().add(ColumnMetadata.builder()
            .name("age")
            .type("INT")
            .build());
        newTableSchemaInfos.add(schemaInfoOfPerson3);

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRemove().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToAdd().isEmpty());

        List<TableChangeDifference> tablesToChange = tableSchemaDifference.getTablesToChange();
        Assert.assertEquals(1, tablesToChange.size());

        TableChangeDifference tableToChange = tablesToChange.get(0);
        TableSchemaInfo newTableSchemaInfo = tableToChange.getNewTableSchemaInfo();
        TableSchemaInfo oldTableSchemaInfo = tableToChange.getOldTableSchemaInfo();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newTableSchemaInfo.getColumnMetadatas(), oldTableSchemaInfo.getColumnMetadatas());
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
        Assert.assertEquals(1, columnMetadataDifference.getColumnsToAdd().size());
        Assert.assertTrue(columnMetadataDifference.getColumnsToChange().isEmpty());
        Assert.assertTrue(columnMetadataDifference.getColumnsToRename().isEmpty());
        Assert.assertTrue(columnMetadataDifference.getColumnsToRemove().isEmpty());
    }

    /**
     * Case 12: Add 1 column to 1 table, rename 1 column, change 1 column, remove 1 column.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos12() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        newTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey2.class));

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        // region Detailed asserts.
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRemove().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToAdd().isEmpty());

        List<TableChangeDifference> tablesToChange = tableSchemaDifference.getTablesToChange();
        Assert.assertEquals(1, tablesToChange.size());

        TableChangeDifference tableToChange = tablesToChange.get(0);
        TableSchemaInfo newTableSchemaInfo = tableToChange.getNewTableSchemaInfo();
        TableSchemaInfo oldTableSchemaInfo = tableToChange.getOldTableSchemaInfo();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newTableSchemaInfo.getColumnMetadatas(), oldTableSchemaInfo.getColumnMetadatas());
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());

        // Column "name", from "VARCHAR(200)" to "VARCHAR(100)".
        List<ColumnChangeDifference> columnsToChange = columnMetadataDifference.getColumnsToChange();
        Assert.assertEquals(1, columnsToChange.size());
        ColumnChangeDifference columnChangeDifference = columnsToChange.get(0);
        Assert.assertEquals("name", columnChangeDifference.getName());
        Assert.assertEquals("VARCHAR(200)", columnChangeDifference.getOldColumnMetadata().getType());
        Assert.assertEquals("VARCHAR(100)", columnChangeDifference.getNewColumnMetadata().getType());

        List<ColumnRenameDifference> columnsToRename = columnMetadataDifference.getColumnsToRename();
        Assert.assertEquals(1, columnsToRename.size());
        ColumnRenameDifference columnRenameDifference = columnsToRename.get(0);
        Assert.assertEquals("birthday", columnRenameDifference.getOldName());
        Assert.assertEquals("birthdate", columnRenameDifference.getNewName());

        List<ColumnMetadata> columnsToAdd = columnMetadataDifference.getColumnsToAdd();
        Assert.assertEquals(1, columnsToAdd.size());
        ColumnMetadata columnMetadata = columnsToAdd.get(0);
        Assert.assertEquals("age", columnMetadata.getName());
        Assert.assertEquals("INT", columnMetadata.getType());
        Assert.assertFalse(columnMetadata.isNullable());

        List<ColumnMetadata> columnsToRemove = columnMetadataDifference.getColumnsToRemove();
        Assert.assertEquals(1, columnsToRemove.size());
        ColumnMetadata columnMetadata2 = columnsToRemove.get(0);
        Assert.assertEquals("gender", columnMetadata2.getName());
        Assert.assertEquals("VARCHAR(32767)", columnMetadata2.getType());
        Assert.assertTrue(columnMetadata2.isNullable());
        // endregion.
    }
}
