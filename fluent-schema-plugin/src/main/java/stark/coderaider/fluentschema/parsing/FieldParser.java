package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.NamingConverter;
import stark.coderaider.fluentschema.commons.annotations.AutoIncrement;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Key;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.schemas.AutoIncrementMetadata;
import stark.coderaider.fluentschema.commons.schemas.ColumnMetadata;
import stark.coderaider.fluentschema.commons.schemas.KeyBuilderInfo;
import stark.coderaider.fluentschema.commons.schemas.PrimaryKeyMetadata;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldParser
{
    public static final String VARCHAR_TYPE_PREFIX = "VARCHAR(";
    public static final String VARCHAR_PATTERN_STRING = "^VARCHAR\\([1-9]\\d*\\)$";

    private static final HashMap<String, String> ACCEPTABLE_TYPE_MAP;
    public static final Pattern VARCHAR_PATTERN;
    public static final String STRING_TYPE;

    public static final List<String> AUTO_INCREMENT_ACCEPTABLE_TYPES;

    private final Field field;

    static
    {
        STRING_TYPE = String.class.getName();
        VARCHAR_PATTERN = Pattern.compile(VARCHAR_PATTERN_STRING);

        ACCEPTABLE_TYPE_MAP = new HashMap<>();

        // region Primitives.
        ACCEPTABLE_TYPE_MAP.put("int", "INT");
        ACCEPTABLE_TYPE_MAP.put("long", "BIGINT");
        ACCEPTABLE_TYPE_MAP.put("float", "FLOAT");
        ACCEPTABLE_TYPE_MAP.put("double", "DOUBLE");
        ACCEPTABLE_TYPE_MAP.put("boolean", "BOOL");
        // endregion

        // region Wrapper classes.
        ACCEPTABLE_TYPE_MAP.put("java.lang.Integer", "INT");
        ACCEPTABLE_TYPE_MAP.put("java.lang.Long", "BIGINT");
        ACCEPTABLE_TYPE_MAP.put("java.lang.Float", "FLOAT");
        ACCEPTABLE_TYPE_MAP.put("java.lang.Double", "DOUBLE");
        ACCEPTABLE_TYPE_MAP.put("java.lang.Boolean", "BOOL");
        // endregion

        ACCEPTABLE_TYPE_MAP.put(STRING_TYPE, "VARCHAR");

        // region Date.
        ACCEPTABLE_TYPE_MAP.put("java.util.Date", "DATETIME");
        ACCEPTABLE_TYPE_MAP.put("java.sql.Date", "DATETIME");
        // endregion

        AUTO_INCREMENT_ACCEPTABLE_TYPES = List.of(
            "INT",
            "BIGINT"
        );
    }

    private final Class<?> fieldType;
    private final String fieldName;
    private final String fieldTypeName;
    private final NamingConvention namingConvention;
    private final String entityClassName;
    private final int varcharMaxLength;

    public FieldParser(Field field, EntityParser entityParser)
    {
        this.field = field;
        fieldType = field.getType();
        fieldName = field.getName();
        fieldTypeName = fieldType.getName();
        namingConvention = entityParser.getNamingConvention();
        entityClassName = entityParser.getEntityClassName();
        varcharMaxLength = entityParser.getVarcharMaxLength();
    }

    public ColumnMetadataWrapper parse() throws MojoExecutionException
    {
        if (!ACCEPTABLE_TYPE_MAP.containsKey(fieldTypeName))
            throw new MojoExecutionException("Unacceptable column type: " + fieldTypeName + ", column = " + fieldName + ", class = " + entityClassName);

        ColumnMetadataWrapper columnMetadataWrapper = new ColumnMetadataWrapper();

        // TODO: Make sure we can have combination primary key in the future.
        // For simplicity, now we assume there is only 1 primary key column.
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        boolean columnIsPrimaryKey = primaryKey != null;

        ColumnMetadata columnMetadata = buildColumnMetadata(columnIsPrimaryKey);

        // Set primary key info if the current column is a column of primary key.
        if (columnIsPrimaryKey)
        {
            PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKeyMetadataBuilder = PrimaryKeyMetadata.builder();
            PrimaryKeyMetadata primaryKeyMetadata = primaryKeyMetadataBuilder.columnName(columnMetadata.getName()).build();
            columnMetadataWrapper.setPrimaryKeyMetadata(primaryKeyMetadata);
        }

        // Encapsulate into a method.
        setAutoIncrement(columnMetadata, columnIsPrimaryKey);

        // Keys.
        List<KeyBuilderInfo> keyBuilderInfos = getKeyBuilderInfos(columnMetadata);

        columnMetadataWrapper.setColumnMetadata(columnMetadata);
        columnMetadataWrapper.setKeyBuilderInfos(keyBuilderInfos);
        return columnMetadataWrapper;
    }

    private void setAutoIncrement(ColumnMetadata columnMetadata, boolean columnIsPrimaryKey) throws MojoExecutionException
    {
        AutoIncrement autoIncrement = field.getAnnotation(AutoIncrement.class);
        if (autoIncrement != null)
        {
            if (!AUTO_INCREMENT_ACCEPTABLE_TYPES.contains(columnMetadata.getType()))
                throw new MojoExecutionException("Auto increment is only supported for INT and BIGINT columns. Field = " + fieldName + ", (class = " + entityClassName + ").");

            boolean unique = columnMetadata.isUnique();
            if (!unique && !columnIsPrimaryKey)
                throw new MojoExecutionException("Auto increment is only supported for primary key or unique columns. Field = " + field.getName() + ", (class = " + entityClassName + ").");

            columnMetadata.setAutoIncrement(new AutoIncrementMetadata(autoIncrement.begin()));
        }
    }

    private ColumnMetadata buildColumnMetadata(boolean columnIsPrimaryKey) throws MojoExecutionException
    {
        boolean fieldTypeIsPrimitive = fieldType.isPrimitive();
        ColumnMetadata.ColumnMetadataBuilder columnMetadataBuilder = ColumnMetadata.builder();

        Column column = field.getAnnotation(Column.class);
        if (column != null)
        {
            // Validate column name.
            String columnName = getAndValidateColumnName(column);
            columnMetadataBuilder.name(columnName);

            String columnType = getAndValidateColumnType(column);
            columnMetadataBuilder.type(columnType);

            boolean nullable = getAndValidateColumnNullable(column, fieldTypeIsPrimitive, columnIsPrimaryKey, entityClassName);
            columnMetadataBuilder.nullable(nullable);

            // Comment is a string, no validation is needed.
            // For default value & trigger of update, it can be NULL, a numeric value or a database function, so we leave it for database to validate.
            columnMetadataBuilder
                .unique(column.unique())
                .comment(column.comment())
                .defaultValue(column.defaultValue())
                .onUpdate(column.onUpdate());
        }
        else
        {
            String columnName = NamingConverter.applyConvention(fieldName, namingConvention);
            columnMetadataBuilder.name(columnName);
            columnMetadataBuilder.nullable(!fieldTypeIsPrimitive);
            columnMetadataBuilder.type(getColumnTypeByFieldType(fieldTypeName, varcharMaxLength));
        }

        return columnMetadataBuilder.build();
    }

    private List<KeyBuilderInfo> getKeyBuilderInfos(ColumnMetadata columnMetadata) throws MojoExecutionException
    {
        List<KeyBuilderInfo> keyBuilderInfos = new ArrayList<>();

        Key[] keys = field.getAnnotationsByType(Key.class);
        if (keys.length > 0)
        {
            for (Key key : keys)
            {
                String name = key.name();
                if (name.isEmpty())
                    throw new MojoExecutionException("Name of an index cannot be empty. Field = " + field.getName() + ", (class = " + entityClassName + ").");

                KeyBuilderInfo keyBuilderInfo = new KeyBuilderInfo();
                keyBuilderInfo.setKey(name);
                keyBuilderInfo.setColumn(columnMetadata);
                keyBuilderInfo.setOrder(key.order());
                keyBuilderInfo.setField(field);

                keyBuilderInfos.add(keyBuilderInfo);
            }
        }

        return keyBuilderInfos;
    }

    private String getAndValidateColumnName(Column column) throws MojoExecutionException
    {
        String columnName = column.name();
        if (columnName.isEmpty())
            return NamingConverter.applyConvention(fieldName, namingConvention);

        try
        {
            String convertedColumnName = NamingConverter.applyConvention(columnName, namingConvention);
            if (!convertedColumnName.equals(columnName))
                throw new MojoExecutionException(MessageFormat.format("The specified column name does not satisfy the naming convention. Column = \"{0}\", field = \"{1}\", class = \"{2}\"", columnName, fieldName, entityClassName));
            return columnName;
        }
        catch (IllegalArgumentException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private static String getColumnTypeByFieldType(String fieldTypeName, int varcharMaxLength)
    {
        if (fieldTypeName.equals(STRING_TYPE))
            return "VARCHAR(" + varcharMaxLength + ")";
        else
            return ACCEPTABLE_TYPE_MAP.get(fieldTypeName);
    }

    private String getAndValidateColumnType(Column column) throws MojoExecutionException
    {
        String columnType = column.type();

        if (columnType.isEmpty())
            columnType = getColumnTypeByFieldType(fieldTypeName, varcharMaxLength);
        else if (fieldTypeName.equals(STRING_TYPE))
        {
            Matcher matcher = VARCHAR_PATTERN.matcher(columnType);
            if (!matcher.matches())
                throw new MojoExecutionException("Unacceptable column type (type mismatch): " + columnType + " for class " + entityClassName + ".");

            String lengthString = columnType.substring(VARCHAR_TYPE_PREFIX.length(), columnType.length() - 1);
            try
            {
                long length = Long.parseLong(lengthString);
                if (length < 1 || length > varcharMaxLength)
                    throw new MojoExecutionException("Unacceptable column type (length exceeded): " + columnType + " for class " + entityClassName + ".");
            }
            catch (NumberFormatException e)
            {
                throw new MojoExecutionException("Unacceptable column type (error parsing length): " + columnType + " for class " + entityClassName + ".", e);
            }
        }
        else
        {
            String correctColumnType = ACCEPTABLE_TYPE_MAP.get(fieldTypeName);
            if (!correctColumnType.equals(columnType))
                throw new MojoExecutionException("Unacceptable column type (type mismatch): " + columnType + " for class " + entityClassName + ".");
        }

        return columnType;
    }

    private boolean getAndValidateColumnNullable(Column column, boolean fieldTypeIsPrimitive, boolean columnIsPrimaryKey, String entityClassName) throws MojoExecutionException
    {
        boolean nullable = column.nullable();
        if (nullable && fieldTypeIsPrimitive && !columnIsPrimaryKey)
            throw new MojoExecutionException("Type " + fieldTypeName + " cannot be nullable (field = " + fieldName + "), class = (" + entityClassName + ").");
        return nullable;
    }
}
