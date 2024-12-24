package stark.coderaider.fluentschema.parsing;

import org.junit.Assert;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.parsing.differences.ColumnMetadataDifference;
import stark.dataworks.basic.data.json.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class CompareColumnMetadatasTest
{
    /**
     * Case 1: No change.
     */
    @Test
    public void testCompareColumnMetadatas1()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName = ColumnMetadata.builder()
            .name("name")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName);
        newColumnMetadatas.add(columnName);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertTrue(columnMetadataDifference.noChange());
    }

    /**
     * Case 2: Rename.
     */
    @Test
    public void testCompareColumnMetadatas2()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName1 = ColumnMetadata.builder()
            .name("name1")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        ColumnMetadata columnName2 = ColumnMetadata.builder()
            .name("name2")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName1);
        newColumnMetadatas.add(columnName2);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }

    /**
     * Case 3: Change column.
     */
    @Test
    public void testCompareColumnMetadatas3()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName1 = ColumnMetadata.builder()
            .name("name")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        ColumnMetadata columnName2 = ColumnMetadata.builder()
            .name("name")
            .type("VARCHAR(20)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName1);
        newColumnMetadatas.add(columnName2);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }

    /**
     * Case 4: Add column.
     */
    @Test
    public void testCompareColumnMetadatas4()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName = ColumnMetadata.builder()
            .name("name")
            .type("VARCHAR(20)")
            .nullable(false)
            .build();
        newColumnMetadatas.add(columnName);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }

    /**
     * Case 5: Remove column.
     */
    @Test
    public void testCompareColumnMetadatas5()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName = ColumnMetadata.builder()
            .name("name")
            .type("VARCHAR(20)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }

    /**
     * Case 6: Add & remove column.
     */
    @Test
    public void testCompareColumnMetadatas6()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName1 = ColumnMetadata.builder()
            .name("name1")
            .type("VARCHAR(20)")
            .nullable(false)
            .build();
        ColumnMetadata columnName2 = ColumnMetadata.builder()
            .name("name2")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName1);
        newColumnMetadatas.add(columnName2);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }

    /**
     * Case 7: Add & remove column, different column count.
     */
    @Test
    public void testCompareColumnMetadatas7()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName1 = ColumnMetadata.builder()
            .name("name1")
            .type("VARCHAR(20)")
            .nullable(false)
            .build();
        ColumnMetadata columnName2 = ColumnMetadata.builder()
            .name("name2")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName1);
        newColumnMetadatas.add(columnName2);

        ColumnMetadata columnBirthday = ColumnMetadata.builder()
            .name("birthday")
            .type("DATETIME")
            .nullable(false)
            .build();
        newColumnMetadatas.add(columnBirthday);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }

    /**
     * Case 8: Add & remove column, different column count.
     */
    @Test
    public void testCompareColumnMetadatas8()
    {
        List<ColumnMetadata> oldColumnMetadatas = new ArrayList<>();
        List<ColumnMetadata> newColumnMetadatas = new ArrayList<>();

        ColumnMetadata columnId = ColumnMetadata.builder()
            .name("id")
            .type("BIGINT")
            .autoIncrement(1)
            .build();
        oldColumnMetadatas.add(columnId);
        newColumnMetadatas.add(columnId);

        ColumnMetadata columnName1 = ColumnMetadata.builder()
            .name("name1")
            .type("VARCHAR(20)")
            .nullable(false)
            .build();
        ColumnMetadata columnName2 = ColumnMetadata.builder()
            .name("name2")
            .type("VARCHAR(200)")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnName1);
        newColumnMetadatas.add(columnName2);

        ColumnMetadata columnBirthday = ColumnMetadata.builder()
            .name("birthday")
            .type("DATETIME")
            .nullable(false)
            .build();
        oldColumnMetadatas.add(columnBirthday);

        ColumnMetadataDifference columnMetadataDifference = TableSchemaInfoComparator.compareColumnMetadatas(newColumnMetadatas, oldColumnMetadatas);
        System.out.println(JsonSerializer.serialize(columnMetadataDifference));
        Assert.assertFalse(columnMetadataDifference.noChange());
    }
}