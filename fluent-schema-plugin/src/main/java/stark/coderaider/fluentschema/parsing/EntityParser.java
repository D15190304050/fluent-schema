package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.annotations.AutoIncrement;
import stark.coderaider.fluentschema.annotations.Column;
import stark.coderaider.fluentschema.annotations.NotMapped;
import stark.coderaider.fluentschema.annotations.Table;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.NamingConverter;
import stark.coderaider.fluentschema.metadata.TableMetadata;
import stark.coderaider.fluentschema.schemas.ColumnInfo;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;
import stark.dataworks.basic.beans.FieldExtractor;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;

public class EntityParser
{
    // TODO: Determine database system type from connection string.
    // Now we assume mysql only.

    public static final String STRING_TYPE = "java.lang.String";
    public static final String INNODB = "InnoDB";
    private static final HashMap<String, String> ACCEPTABLE_TYPE_MAP;

    static
    {
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
    }

    public TableSchemaInfo parse(Class<?> entityClass) throws MojoExecutionException
    {
        String entityClassName = entityClass.getName();

        Table table = entityClass.getAnnotation(Table.class);
        TableMetadata tableMetadata = toTableMetadata(entityClass, table);

        NamingConvention namingConvention = tableMetadata.getNamingConvention();
        String tableName = getTableName(entityClass, table, namingConvention);

        boolean isInnoDb = tableMetadata.getEngine().equals(INNODB);
        int varcharMaxLength = isInnoDb ? 32767 : 65535;

        boolean hasAutoIncrement = false;

        List<Field> fields = FieldExtractor.getAllFields(entityClass);
        for (Field field : fields)
        {
            NotMapped notMapped = field.getAnnotation(NotMapped.class);
            if (notMapped != null)
            {
                Class<?> fieldType = field.getType();
                ColumnInfo.ColumnInfoBuilder columnInfoBuilder = ColumnInfo.builder();

                AutoIncrement autoIncrement = field.getAnnotation(AutoIncrement.class);
                if (autoIncrement != null)
                {
                    if (hasAutoIncrement)
                        throw new MojoExecutionException(MessageFormat.format("There are more than 1 auto increment columns in table \"{0}\" (class = {1})", tableName, entityClassName));

                    hasAutoIncrement = true;
                    columnInfoBuilder.autoIncrement(autoIncrement.begin(), autoIncrement.increment());
                }

                Column column = field.getAnnotation(Column.class);

                if (column != null)
                {
                    // Validate column name.
                    String columnName = column.name();
                    String columnClassLikeName = NamingConverter.toClassLikeName(columnName);
                    if (!columnName.equals(columnClassLikeName))
                        throw new MojoExecutionException(MessageFormat.format("Invalid column name \"{0}\" for field \"{1}\" in table \"{2}\" (class = {3})", columnName, field.getName(), tableName, entityClassName));

                    columnInfoBuilder
                        .name(columnName)
                        .type(column.type())
                        .nullable(column.nullable())
                        .comment(column.comment())
                        .defaultValue(column.defaultValue())
                        .onUpdate(column.onUpdate());
                }
                else
                {
                    String fieldName = field.getName();
                    String columnName = NamingConverter.applyConvention(fieldName, namingConvention);
                    columnInfoBuilder.name(columnName);
                    columnInfoBuilder.nullable(!fieldType.isPrimitive());

                    if (fieldType.getName().equals(STRING_TYPE))
                        columnInfoBuilder.type("VARCHAR(" + varcharMaxLength + ")");
                }
            }
        }

        return null;
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
                String candidateTableNameBeforeConversion = tableNameInAnnotation == null ? entityClass.getSimpleName() : tableNameInAnnotation;
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
