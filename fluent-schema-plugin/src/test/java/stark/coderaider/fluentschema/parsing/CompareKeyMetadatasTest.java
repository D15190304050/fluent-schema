package stark.coderaider.fluentschema.parsing;

import org.junit.Assert;
import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.KeyMetadata;
import stark.coderaider.fluentschema.parsing.differences.KeyMetadataDifference;
import stark.dataworks.basic.data.json.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class CompareKeyMetadatasTest
{
    /**
     * Case 1: no change.
     */
    @Test
    public void testCompareKeyMetadatas1()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key4 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of(key1, key2);
        List<KeyMetadata> keys2 = List.of(key3, key4);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertTrue(keyMetadataDifference.noChange());
    }

    /**
     * Case 2: Add 1 key.
     */
    @Test
    public void testCompareKeyMetadatas2()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        List<KeyMetadata> keys1 = List.of(key1, key2);
        List<KeyMetadata> keys2 = List.of(key3);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(1, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAlter().size());
    }

    /**
     * Case 3: Add 2 keys.
     */
    @Test
    public void testCompareKeyMetadatas3()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of(key1, key2);
        List<KeyMetadata> keys2 = new ArrayList<>();

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(2, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAlter().size());
    }

    /**
     * Case 4: Drop 1 key.
     */
    @Test
    public void testCompareKeyMetadatas4()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key4 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of(key2);
        List<KeyMetadata> keys2 = List.of(key3, key4);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(1, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAlter().size());
    }

    /**
     * Case 5: Drop 2 keys.
     */
    @Test
    public void testCompareKeyMetadatas5()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key4 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of();
        List<KeyMetadata> keys2 = List.of(key3, key4);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(2, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAlter().size());
    }

    /**
     * Case 6: Alter 1 key.
     */
    @Test
    public void testCompareKeyMetadatas6()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key4 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of(key1, key2);
        List<KeyMetadata> keys2 = List.of(key3, key4);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(1, keyMetadataDifference.getKeysToAlter().size());
        System.out.println(JsonSerializer.serialize(keyMetadataDifference));
    }

    /**
     * Case 7: Alter 2 keys.
     */
    @Test
    public void testCompareKeyMetadatas7()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key4 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of(key1, key2);
        List<KeyMetadata> keys2 = List.of(key3, key4);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(0, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(2, keyMetadataDifference.getKeysToAlter().size());
        System.out.println(JsonSerializer.serialize(keyMetadataDifference));
    }

    /**
     * Case 8: Add 1 key, drop 1 key, alter 1 key.
     */
    @Test
    public void testCompareKeyMetadatas8()
    {
        KeyMetadata key1 = KeyMetadata.builder()
            .name("key1")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key2 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key3 = KeyMetadata.builder()
            .name("key3")
            .columns(List.of("c1", "c2"))
            .build();

        KeyMetadata key4 = KeyMetadata.builder()
            .name("key2")
            .columns(List.of("c1", "c2", "c3"))
            .build();

        List<KeyMetadata> keys1 = List.of(key1, key2);
        List<KeyMetadata> keys2 = List.of(key3, key4);

        KeyMetadataDifference keyMetadataDifference = TableSchemaInfoComparator.compareKeyMetadatas(keys1, keys2);
        Assert.assertFalse(keyMetadataDifference.noChange());
        Assert.assertEquals(1, keyMetadataDifference.getKeysToAdd().size());
        Assert.assertEquals(1, keyMetadataDifference.getKeysToDrop().size());
        Assert.assertEquals(1, keyMetadataDifference.getKeysToAlter().size());
        System.out.println(JsonSerializer.serialize(keyMetadataDifference));
    }
}
