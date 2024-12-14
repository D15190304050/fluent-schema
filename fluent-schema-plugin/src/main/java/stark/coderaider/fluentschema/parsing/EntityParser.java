package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.NamingConverter;
import stark.coderaider.fluentschema.commons.annotations.*;
import stark.coderaider.fluentschema.commons.metadata.PrimaryKeyMetadata;
import stark.coderaider.fluentschema.commons.metadata.TableMetadata;
import stark.coderaider.fluentschema.commons.metadata.ColumnMetadata;
import stark.coderaider.fluentschema.schemas.TableSchemaMetadata;
import stark.dataworks.basic.beans.FieldExtractor;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityParser
{
    // TODO: Determine database system type from connection string.
    // Now we assume mysql only.

    public static final String STRING_TYPE = "java.lang.String";
    public static final String INNODB = "InnoDB";
    public static final String VARCHAR_PATTERN_STRING = "^VARCHAR\\([1-9]\\d*\\)$";
    public static final String VARCHAR_TYPE_PREFIX = "VARCHAR(";
    public static final Pattern VARCHAR_PATTERN;
    private static final HashMap<String, String> ACCEPTABLE_TYPE_MAP;
    public static final List<String> AUTO_INCREMENT_ACCEPTABLE_TYPES;

    static
    {
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

    public TableSchemaMetadata parse(Class<?> entityClass) throws MojoExecutionException
    {
        TableSchemaMetadata tableSchemaMetadata = new TableSchemaMetadata();
        String entityClassName = entityClass.getName();

        Table table = entityClass.getAnnotation(Table.class);
        TableMetadata tableMetadata = toTableMetadata(entityClass, table);

        NamingConvention namingConvention = tableMetadata.getNamingConvention();
        String tableName = getTableName(entityClass, table, namingConvention);

        boolean isInnoDb = tableMetadata.getEngine().equals(INNODB);
        int varcharMaxLength = isInnoDb ? 32767 : 65535;

        boolean hasAutoIncrement = false;
        boolean hasPrimaryKey = false;

        List<ColumnMetadata> columnMetadatas = new ArrayList<>();
        List<Field> fields = FieldExtractor.getAllFields(entityClass);
        for (Field field : fields)
        {
            NotMapped notMapped = field.getAnnotation(NotMapped.class);
            if (notMapped == null)
            {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                String fieldTypeName = fieldType.getName();
                boolean fieldTypeIsPrimitive = fieldType.isPrimitive();
                ColumnMetadata.ColumnMetadataBuilder columnMetadataBuilder = ColumnMetadata.builder();

                if (!ACCEPTABLE_TYPE_MAP.containsKey(fieldTypeName))
                    throw new MojoExecutionException("Unacceptable column type: " + fieldTypeName + ", column = " + fieldName + ", class = " + entityClassName);

                // TODO: Make sure we can have combination primary key in the future.
                // For simplicity, now we assume there is only 1 primary key column.
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                boolean columnIsPrimaryKey = primaryKey != null;
                if (columnIsPrimaryKey)
                {
                    if (hasPrimaryKey)
                        throw new MojoExecutionException(MessageFormat.format("There are more than 1 auto increment columns in table \"{0}\" (class = {1})", tableName, entityClassName));

                    hasPrimaryKey = true;
                }

                AutoIncrement autoIncrement = field.getAnnotation(AutoIncrement.class);
                if (autoIncrement != null)
                {
                    if (hasAutoIncrement)
                        throw new MojoExecutionException(MessageFormat.format("There are more than 1 auto increment columns in table \"{0}\" (class = {1})", tableName, entityClassName));

                    hasAutoIncrement = true;
                    columnMetadataBuilder.autoIncrement(autoIncrement.begin(), autoIncrement.increment());
                }

                Column column = field.getAnnotation(Column.class);
                if (column != null)
                {
                    // Validate column name.
                    String columnName = getAndValidateColumnName(column, fieldName, namingConvention, entityClassName);
                    columnMetadataBuilder.name(columnName);

                    String columnType = getAndValidateColumnType(column, fieldTypeName, varcharMaxLength, entityClassName);
                    columnMetadataBuilder.type(columnType);

                    boolean nullable = getAndValidateColumnNullable(column, fieldName, fieldTypeName, fieldTypeIsPrimitive, columnIsPrimaryKey, entityClassName);
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

                ColumnMetadata columnMetadata = columnMetadataBuilder.build();
                columnMetadatas.add(columnMetadata);

                // Set primary key info if the current column is a column of primary key.
                if (columnIsPrimaryKey)
                {
                    PrimaryKeyMetadata.PrimaryKeyMetadataBuilder primaryKeyMetadataBuilder = PrimaryKeyMetadata.builder();
                    PrimaryKeyMetadata primaryKeyMetadata = primaryKeyMetadataBuilder.columnName(columnMetadata.getName()).build();
                    tableSchemaMetadata.setPrimaryKeyMetadata(primaryKeyMetadata);
                }
            }
        }

        tableSchemaMetadata.setName(tableName);
        tableSchemaMetadata.setColumnMetadatas(columnMetadatas);
        return tableSchemaMetadata;
    }

    private static boolean getAndValidateColumnNullable(Column column, String fieldName, String fieldTypeName, boolean fieldTypeIsPrimitive, boolean columnIsPrimaryKey, String entityClassName) throws MojoExecutionException
    {
        boolean nullable = column.nullable();
        if (nullable && fieldTypeIsPrimitive && !columnIsPrimaryKey)
            throw new MojoExecutionException("Type " + fieldTypeName + " cannot be nullable (field = " + fieldName + "), class = (" + entityClassName + ").");
        return nullable;
    }

    private static String getAndValidateColumnType(Column column, String fieldTypeName, int varcharMaxLength, String entityClassName) throws MojoExecutionException
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

    private static String getColumnTypeByFieldType(String fieldTypeName, int varcharMaxLength)
    {
        if (fieldTypeName.equals(STRING_TYPE))
            return "VARCHAR(" + varcharMaxLength + ")";
        else
            return ACCEPTABLE_TYPE_MAP.get(fieldTypeName);
    }

    private static String getAndValidateColumnName(Column column, String fieldName, NamingConvention namingConvention, String entityClassName) throws MojoExecutionException
    {
        String columnName = column.name();
        if (columnName.isEmpty())
            columnName = fieldName;

        try
        {
            String convertedColumnName = NamingConverter.applyConvention(columnName, namingConvention);
            if (!convertedColumnName.equals(columnName))
                throw new MojoExecutionException(MessageFormat.format("The specified column name does not satisfy the naming convention. Column = \"{0}\", field = \"{1}\", class = \"{2}\"", columnName, fieldName, entityClassName));
        }
        catch (IllegalArgumentException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        return columnName;
    }

    private static String getTableName(Class<?> entityClass, Table table, NamingConvention namingConvention) throws MojoExecutionException
    {
        String tableName;
        if (table == null)
            tableName = entityClass.getSimpleName();
        else
        {
            try
            {
                String tableNameInAnnotation = table.name();
                String candidateTableNameBeforeConversion = tableNameInAnnotation.isEmpty() ? entityClass.getSimpleName() : tableNameInAnnotation;
                tableName = NamingConverter.applyConvention(candidateTableNameBeforeConversion, namingConvention);
            }
            catch (IllegalArgumentException e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

        return tableName;
    }

    private static TableMetadata toTableMetadata(Class<?> entityClass, Table table)
    {
        TableMetadata tableMetadata = new TableMetadata();

        if (table == null)
        {
            tableMetadata.setName(entityClass.getSimpleName());
            tableMetadata.setNamingConvention(NamingConvention.RAW);
            tableMetadata.setEngine(INNODB);
        }
        else
        {
            tableMetadata.setName(table.name());
            tableMetadata.setNamingConvention(table.namingConvention());
            tableMetadata.setEngine(table.engine());
            tableMetadata.setComment(table.comment());
        }

        return tableMetadata;
    }
}
