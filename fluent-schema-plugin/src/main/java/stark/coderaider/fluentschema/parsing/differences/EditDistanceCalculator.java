package stark.coderaider.fluentschema.parsing.differences;

import stark.coderaider.fluentschema.commons.metadata.AutoIncrementMetadata;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.commons.metadata.KeyMetadata;
import stark.coderaider.fluentschema.commons.metadata.PrimaryKeyMetadata;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class EditDistanceCalculator
{
    private EditDistanceCalculator(){}

    /**
     * Gets the edit distance of 2 column metadata. Here we compare all fields except the name.
     * This method will return how many fields are different in 2 {@link TableSchemaInfo} instances.
     * @param left An instance of the {@link TableSchemaInfo} class.
     * @param right The other instance of the {@link TableSchemaInfo} class.
     * @return How many fields are different in 2 {@link TableSchemaInfo} instances.
     */
    public static int getEditDistance(TableSchemaInfo left, TableSchemaInfo right)
    {
        if (left == null && right == null)
            return 0;

        if (left == null && right != null)
            return 1;

        if (left != null && right == null)
            return 1;

        if (left == right || left.equals(right))
            return 0;

        int editDistance = 0;
        editDistance += editDistanceOfStringField(left, right, TableSchemaInfo::getComment);
        editDistance += editDistanceOfStringField(left, right, TableSchemaInfo::getEngine);

        PrimaryKeyMetadata primaryKeyMetadataLeft = left.getPrimaryKeyMetadata();
        PrimaryKeyMetadata primaryKeyMetadataRight = right.getPrimaryKeyMetadata();
        if (!primaryKeyMetadataLeft.equals(primaryKeyMetadataRight))
            editDistance++;

        List<ColumnMetadata> columnMetadatasLeft = left.getColumnMetadatas();
        List<ColumnMetadata> columnMetadatasRight = right.getColumnMetadatas();
        Map<String, ColumnMetadata> columnMetadataMapLeft = columnMetadatasLeft.stream().collect(Collectors.toMap(ColumnMetadata::getName, Function.identity()));
        Map<String, ColumnMetadata> columnMetadataMapRight = columnMetadatasRight.stream().collect(Collectors.toMap(ColumnMetadata::getName, Function.identity()));
        HashSet<String> columnNames = new HashSet<>(columnMetadataMapLeft.keySet());
        columnNames.addAll(columnMetadataMapRight.keySet());
        for (String columnName : columnNames)
        {
            ColumnMetadata columnMetadataLeft = columnMetadataMapLeft.get(columnName);
            ColumnMetadata columnMetadataRight = columnMetadataMapRight.get(columnName);

            editDistance += getEditDistance(columnMetadataLeft, columnMetadataRight);
        }

        List<KeyMetadata> keyMetadatasLeft = left.getKeyMetadatas();
        List<KeyMetadata> keyMetadatasRight = right.getKeyMetadatas();
        Map<String, KeyMetadata> keyMetadataMapLeft = keyMetadatasLeft.stream().collect(Collectors.toMap(KeyMetadata::getName, Function.identity()));
        Map<String, KeyMetadata> keyMetadataMapRight = keyMetadatasRight.stream().collect(Collectors.toMap(KeyMetadata::getName, Function.identity()));
        HashSet<String> keyNames = new HashSet<>(keyMetadataMapLeft.keySet());
        keyNames.addAll(keyMetadataMapRight.keySet());
        for (String keyName : keyNames)
        {
            KeyMetadata keyMetadataLeft = keyMetadataMapLeft.get(keyName);
            KeyMetadata keyMetadataRight = keyMetadataMapRight.get(keyName);

            if (keyMetadataLeft != null && keyMetadataRight != null)
            {
                if (!keyMetadataLeft.equals(keyMetadataRight))
                    editDistance++;
            }

            if (keyMetadataLeft != null && keyMetadataRight == null)
                editDistance++;

            if (keyMetadataLeft == null && keyMetadataRight != null)
                editDistance++;
        }

        return editDistance;
    }

    /**
     * Gets the edit distance of 2 column metadata. Here we compare all fields except the name.
     * This method will return how many fields are different in 2 {@link ColumnMetadata} instances.
     * @param left An instance of the {@link ColumnMetadata} class.
     * @param right The other instance of the {@link ColumnMetadata} class.
     * @return How many fields are different in 2 {@link ColumnMetadata} instances.
     */
    public static int getEditDistance(ColumnMetadata left, ColumnMetadata right)
    {
        if (left == null && right == null)
            return 0;

        if (left == right)
            return 0;

        if (left == null && right != null)
            return Integer.MAX_VALUE;

        if (left != null && right == null)
            return Integer.MAX_VALUE;

        if (left.equals(right))
            return 0;

        int editDistance = 0;

//        if (!left.getName().equals(right.getName()))
//            editDistance++;

        if (!left.getType().equals(right.getType()))
            editDistance++;

        if (left.isNullable() != right.isNullable())
            editDistance++;

        if (left.isUnique() != right.isUnique())
            editDistance++;

        editDistance += editDistanceOfStringField(left, right, ColumnMetadata::getComment);
        editDistance += editDistanceOfStringField(left, right, ColumnMetadata::getDefaultValue);
        editDistance += editDistanceOfStringField(left, right, ColumnMetadata::getOnUpdate);

        AutoIncrementMetadata autoIncrementLeft = left.getAutoIncrement();
        AutoIncrementMetadata autoIncrementRight = right.getAutoIncrement();
        editDistance += editDistanceOfAutoIncrement(autoIncrementLeft, autoIncrementRight);

        return editDistance;
    }

    public static <T> int editDistanceOfStringField(T left, T right, Function<T, String> fieldGetter)
    {
        String fieldValueLeft = fieldGetter.apply(left);
        fieldValueLeft = fieldValueLeft == null ? "" : fieldValueLeft;
        String fieldValueRight = fieldGetter.apply(right);
        fieldValueRight = fieldValueRight == null ? "" : fieldValueRight;

        return fieldValueLeft.equals(fieldValueRight) ? 0 : 1;
    }

    private static int editDistanceOfAutoIncrement(AutoIncrementMetadata left, AutoIncrementMetadata right)
    {
        if (left == null && right == null)
            return 0;

        if (left == right)
            return 0;

        if (left == null && right != null)
            return 1;

        if (left != null && right == null)
            return 1;

        if (left.getBegin() != right.getBegin())
            return 1;

        if (left.getIncrement() != right.getIncrement())
            return 1;

        return 0;
    }
}
