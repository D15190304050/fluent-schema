package stark.coderaider.fluentschema.parsing.differences;

import stark.coderaider.fluentschema.commons.metadata.AutoIncrementMetadata;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;

import java.util.function.Function;

public final class ColumnMetadataEditDistance
{
    private ColumnMetadataEditDistance()
    {

    }

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

        if (!left.getName().equals(right.getName()))
            editDistance++;

        if (!left.getType().equals(right.getType()))
            editDistance++;

        if (left.isNullable() != right.isNullable())
            editDistance++;

        if (left.isUnique() != right.isUnique())
            editDistance++;

        editDistance += editDistanceOfField(left, right, ColumnMetadata::getComment);
        editDistance += editDistanceOfField(left, right, ColumnMetadata::getDefaultValue);
        editDistance += editDistanceOfField(left, right, ColumnMetadata::getOnUpdate);

        AutoIncrementMetadata autoIncrementLeft = left.getAutoIncrement();
        AutoIncrementMetadata autoIncrementRight = right.getAutoIncrement();
        editDistance += editDistanceOfAutoIncrement(autoIncrementLeft, autoIncrementRight);

        return editDistance;
    }

    private static int editDistanceOfField(ColumnMetadata left, ColumnMetadata right, Function<ColumnMetadata, String> fieldGetter)
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
