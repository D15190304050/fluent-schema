package stark.coderaider.fluentschema.parsing;

import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.annotations.AutoIncrement;
import stark.coderaider.fluentschema.annotations.Column;
import stark.coderaider.fluentschema.annotations.NotMapped;
import stark.coderaider.fluentschema.annotations.Table;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.schemas.ColumnInfo;
import stark.coderaider.fluentschema.schemas.TableSchemaInfo;
import stark.dataworks.basic.beans.FieldExtractor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class EntityParser
{
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

        ACCEPTABLE_TYPE_MAP.put("java.lang.String", "VARCHAR");

        // region Date.
        ACCEPTABLE_TYPE_MAP.put("java.util.Date", "DATETIME");
        ACCEPTABLE_TYPE_MAP.put("java.sql.Date", "DATETIME");
        // endregion
    }

    public TableSchemaInfo parse(Class<?> entityClass) throws MojoExecutionException
    {
        Table table = entityClass.getAnnotation(Table.class);
        String tableName;
        if (table == null)
            tableName = entityClass.getSimpleName();
        else
        {
            tableName = table.name();
            String classLikeTableName = NamingConvention.convertToClassLikeName(tableName);
            if (!tableName.equals(classLikeTableName))
                throw new MojoExecutionException("\"" + classLikeTableName + "\" is not a valid table name.");
        }

        List<Field> fields = FieldExtractor.getAllFields(entityClass);
        for (Field field : fields)
        {
            NotMapped notMapped = field.getAnnotation(NotMapped.class);
            if (notMapped != null)
            {
                AutoIncrement autoIncrement = field.getAnnotation(AutoIncrement.class);
                Column column = field.getAnnotation(Column.class);

                ColumnInfo.ColumnInfoBuilder columnInfoBuilder = ColumnInfo.builder();
                if (column != null)
                {
                    String columnName = column.name();
                    String columnClassLikeName = NamingConvention.convertToClassLikeName(columnName);
                    if (!columnName.equals(columnClassLikeName))
                    {
                        throw new MojoExecutionException("Invalid column name \"" + columnName + "\" for field '" + field.getName() + "' in table " + tableName + ".");
                    }

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
                    String name = field.getName();
                    columnInfoBuilder.name(name);


                }
            }
        }

        return null;
    }
}
