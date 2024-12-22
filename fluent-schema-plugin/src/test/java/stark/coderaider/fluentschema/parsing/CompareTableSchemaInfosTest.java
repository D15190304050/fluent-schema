package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.parsing.differences.*;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.test.entities.*;
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
        TableSchemaDifference correct = JsonSerializer.deserialize("{\"tablesToAlter\":[],\"tablesToRename\":[{\"oldName\":\"person_with_combination_key\",\"newName\":\"PersonWithCombinationKey\"}],\"tablesToAdd\":[],\"tablesToDrop\":[]}", TableSchemaDifference.class);
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

        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        TableChangeDifference tableToAlter = tablesToAlter.get(0);
        TableSchemaInfo oldTableSchemaInfo = tableToAlter.getOldTableSchemaInfo();
        TableSchemaInfo newTableSchemaInfo = tableToAlter.getNewTableSchemaInfo();

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

        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        TableChangeDifference tableToAlter = tablesToAlter.get(0);
        TableSchemaInfo oldTableSchemaInfo = tableToAlter.getOldTableSchemaInfo();
        TableSchemaInfo newTableSchemaInfo = tableToAlter.getNewTableSchemaInfo();

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
        Assert.assertTrue(tableSchemaDifference.getTablesToDrop().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToAlter().isEmpty());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToDrop().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToAlter().isEmpty());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToAlter().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToDrop().size());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToAlter().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(2, tableSchemaDifference.getTablesToDrop().size());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToAlter().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToRename().isEmpty());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToAdd().size());
        Assert.assertEquals(1, tableSchemaDifference.getTablesToDrop().size());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToAlter().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToDrop().isEmpty());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToDrop().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToAdd().isEmpty());

        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        Assert.assertEquals(1, tablesToAlter.size());

        TableChangeDifference tableToAlter = tablesToAlter.get(0);
        TableSchemaInfo newTableSchemaInfo = tableToAlter.getNewTableSchemaInfo();
        TableSchemaInfo oldTableSchemaInfo = tableToAlter.getOldTableSchemaInfo();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newTableSchemaInfo.getColumnMetadatas(), oldTableSchemaInfo.getColumnMetadatas());
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
        Assert.assertEquals(1, columnMetadataDifference.getColumnsToAdd().size());
        Assert.assertTrue(columnMetadataDifference.getColumnsToAlter().isEmpty());
        Assert.assertTrue(columnMetadataDifference.getColumnsToRename().isEmpty());
        Assert.assertTrue(columnMetadataDifference.getColumnsToDrop().isEmpty());
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
        Assert.assertTrue(tableSchemaDifference.getTablesToDrop().isEmpty());
        Assert.assertTrue(tableSchemaDifference.getTablesToAdd().isEmpty());

        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        Assert.assertEquals(1, tablesToAlter.size());

        TableChangeDifference tableToAlter = tablesToAlter.get(0);
        TableSchemaInfo newTableSchemaInfo = tableToAlter.getNewTableSchemaInfo();
        TableSchemaInfo oldTableSchemaInfo = tableToAlter.getOldTableSchemaInfo();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newTableSchemaInfo.getColumnMetadatas(), oldTableSchemaInfo.getColumnMetadatas());
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());

        // Column "name", from "VARCHAR(200)" to "VARCHAR(100)".
        List<ColumnAlterDifference> columnsToAlter = columnMetadataDifference.getColumnsToAlter();
        Assert.assertEquals(1, columnsToAlter.size());
        ColumnAlterDifference columnAlterDifference = columnsToAlter.get(0);
        Assert.assertEquals("name", columnAlterDifference.getName());
        Assert.assertEquals("VARCHAR(200)", columnAlterDifference.getOldColumnMetadata().getType());
        Assert.assertEquals("VARCHAR(100)", columnAlterDifference.getNewColumnMetadata().getType());

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

        List<ColumnMetadata> columnsToDrop = columnMetadataDifference.getColumnsToDrop();
        Assert.assertEquals(1, columnsToDrop.size());
        ColumnMetadata columnMetadata2 = columnsToDrop.get(0);
        Assert.assertEquals("gender", columnMetadata2.getName());
        Assert.assertEquals("VARCHAR(32767)", columnMetadata2.getType());
        Assert.assertTrue(columnMetadata2.isNullable());
        // endregion.
    }

    /**
     * Case 13: Change comment, engine & key name.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos13() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        TableSchemaInfo tableSchemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey3.class);
        tableSchemaInfoOfPerson3.setComment("Comment of PersonWithCombinationKey.");
        tableSchemaInfoOfPerson3.setEngine("MyISAM");

        newTableSchemaInfos.add(tableSchemaInfoOfPerson3);

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        TableChangeDifference tableToAlter = tablesToAlter.get(0);
        TableSchemaInfo oldTableSchemaInfo = tableToAlter.getOldTableSchemaInfo();
        TableSchemaInfo newTableSchemaInfo = tableToAlter.getNewTableSchemaInfo();

        List<ColumnMetadata> oldColumnMetadatas = oldTableSchemaInfo.getColumnMetadatas();
        List<ColumnMetadata> newColumnMetadatas = newTableSchemaInfo.getColumnMetadatas();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        Assert.assertFalse(columnMetadataDifference.noChange());

        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getName));

        Assert.assertEquals(newTableSchemaInfo.getPrimaryKeyMetadata(), oldTableSchemaInfo.getPrimaryKeyMetadata());
        Assert.assertNotEquals(newTableSchemaInfo.getKeyMetadatas(), oldTableSchemaInfo.getKeyMetadatas());

        Assert.assertEquals(1, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getComment));
        Assert.assertEquals(1, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getEngine));
    }

    /**
     * Case 14: Change key column.
     * @throws MojoExecutionException
     */
    @Test
    public void testCompareTableSchemaInfos14() throws MojoExecutionException
    {
        List<TableSchemaInfo> oldTableSchemaInfos = new ArrayList<>();
        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();

        EntityParser entityParser = new EntityParser();
        oldTableSchemaInfos.add(entityParser.parse(Person.class));
        newTableSchemaInfos.add(entityParser.parse(Person.class));

        oldTableSchemaInfos.add(entityParser.parse(PersonWithCombinationKey.class));
        TableSchemaInfo tableSchemaInfoOfPerson3 = entityParser.parse(PersonWithCombinationKey4.class);
        newTableSchemaInfos.add(tableSchemaInfoOfPerson3);

        TableSchemaDifference tableSchemaDifference = TableSchemaInfoComparator.compareTableSchemaInfos(newTableSchemaInfos, oldTableSchemaInfos);
        System.out.println(JsonSerializer.serialize(tableSchemaDifference));
        Assert.assertFalse(tableSchemaDifference.noChange());

        List<TableChangeDifference> tablesToAlter = tableSchemaDifference.getTablesToAlter();
        TableChangeDifference tableToAlter = tablesToAlter.get(0);
        TableSchemaInfo oldTableSchemaInfo = tableToAlter.getOldTableSchemaInfo();
        TableSchemaInfo newTableSchemaInfo = tableToAlter.getNewTableSchemaInfo();

        List<ColumnMetadata> oldColumnMetadatas = oldTableSchemaInfo.getColumnMetadatas();
        List<ColumnMetadata> newColumnMetadatas = newTableSchemaInfo.getColumnMetadatas();
        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        Assert.assertTrue(columnMetadataDifference.noChange());

        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getName));

        Assert.assertEquals(newTableSchemaInfo.getPrimaryKeyMetadata(), oldTableSchemaInfo.getPrimaryKeyMetadata());
        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getComment));
        Assert.assertEquals(0, EditDistanceCalculator.editDistanceOfStringField(newTableSchemaInfo, oldTableSchemaInfo, TableSchemaInfo::getEngine));

        List<KeyMetadata> newKeys = newTableSchemaInfo.getKeyMetadatas();
        List<KeyMetadata> oldKeys = oldTableSchemaInfo.getKeyMetadatas();
        Assert.assertNotEquals(newKeys, oldKeys);

        KeyMetadata newKey = newKeys.get(0);
        KeyMetadata oldKey = oldKeys.get(0);
        List<String> newKeyColumns = newKey.getColumns();
        List<String> oldKeyColumns = oldKey.getColumns();
        Assert.assertEquals(newKey.getName(), oldKey.getName());
        Assert.assertEquals(1, newKeyColumns.size());
        Assert.assertEquals(2, oldKey.getColumns().size());
        Assert.assertTrue(newKeyColumns.contains("name"));
        Assert.assertTrue(oldKeyColumns.contains("name"));
        Assert.assertTrue(oldKeyColumns.contains("birth_place"));
    }
}
