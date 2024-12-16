package stark.coderaider.fluentschema.parsing.differences;

import org.junit.Test;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;

import static org.junit.Assert.*;

public class ColumnMetadataEditDistanceTest
{
    @Test
    public void testGetEditDistance1()
    {
        ColumnMetadata left = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(200)")
            .build();

        ColumnMetadata right = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(200)")
            .build();

        int editDistance = ColumnMetadataEditDistance.getEditDistance(left, right);
        assertEquals(0, editDistance);
    }

    @Test
    public void testGetEditDistance2()
    {
        ColumnMetadata left = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(20)")
            .build();

        ColumnMetadata right = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(200)")
            .build();

        int editDistance = ColumnMetadataEditDistance.getEditDistance(left, right);
        assertEquals(1, editDistance);
    }

    @Test
    public void testGetEditDistance3()
    {
        ColumnMetadata left = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(20)")
            .comment("Table of person.")
            .build();

        ColumnMetadata right = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(200)")
            .build();

        int editDistance = ColumnMetadataEditDistance.getEditDistance(left, right);
        assertEquals(2, editDistance);
    }

    @Test
    public void testGetEditDistance4()
    {
        ColumnMetadata left = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(20)")
            .comment("Table of person.")
            .build();

        ColumnMetadata right = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(200)")
            .nullable(true)
            .build();

        int editDistance = ColumnMetadataEditDistance.getEditDistance(left, right);
        assertEquals(3, editDistance);
    }

    @Test
    public void testGetEditDistance5()
    {
        ColumnMetadata left = ColumnMetadata.builder()
            .name("person")
            .type("VARCHAR(20)")
            .comment("Table of person.")
            .build();

        ColumnMetadata right = null;

        int editDistance = ColumnMetadataEditDistance.getEditDistance(left, right);
        assertEquals(Integer.MAX_VALUE, editDistance);
    }
}