package stark.coderaider.fluentschema.parsing;

import lombok.Getter;
import org.apache.maven.plugin.MojoExecutionException;
import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.NamingConverter;
import stark.coderaider.fluentschema.commons.annotations.*;
import stark.coderaider.fluentschema.commons.schemas.*;
import stark.dataworks.basic.beans.FieldExtractor;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

@Getter
public class EntityParser
{
    // TODO: Determine database system type from connection string.
    // Now we assume mysql only.

    public static final String INNODB = "InnoDB";

    private final Class<?> entityClass;
    private final String entityClassName;
    private final String entityClassSimpleName;
    private final NamingConvention namingConvention;
    private final String tableName;
    private final TableMetadata tableMetadata;
    private final Table table;
    private final TableSchemaInfo tableSchemaInfo;
    private final int varcharMaxLength;

    public EntityParser(Class<?> entityClass) throws MojoExecutionException
    {
        this.entityClass = entityClass;
        table = entityClass.getAnnotation(Table.class);
        entityClassName = entityClass.getName();
        entityClassSimpleName = entityClass.getSimpleName();
        tableMetadata = toTableMetadata();
        namingConvention = tableMetadata.getNamingConvention();
        tableName = getTableName();

        // Calculate max length of VARCHAR.
        boolean isInnoDb = tableMetadata.getEngine().equals(INNODB);
        varcharMaxLength = isInnoDb ? 32767 : 65535;

        tableSchemaInfo = parse();
    }

    public static TableSchemaInfo parse(Class<?> entityClass) throws MojoExecutionException
    {
        return new EntityParser(entityClass).getTableSchemaInfo();
    }

    private TableSchemaInfo parse() throws MojoExecutionException
    {
        boolean hasAutoIncrement = false;
        boolean hasPrimaryKey = false;

        List<ColumnMetadata> columnMetadatas = new ArrayList<>();
        HashMap<String, List<KeyBuilderInfo>> keyMetadataMap = new HashMap<>();
        PrimaryKeyMetadata primaryKeyMetadata = null;
        List<Field> fields = FieldExtractor.getAllFields(entityClass);
        for (Field field : fields)
        {
            NotMapped notMapped = field.getAnnotation(NotMapped.class);
            if (notMapped == null)
            {
                FieldParser fieldParser = new FieldParser(field, this);
                ColumnMetadataWrapper columnMetadataWrapper = fieldParser.parse();

                // Set primary key info.
                PrimaryKeyMetadata primaryKeyMetadataFromField = columnMetadataWrapper.getPrimaryKeyMetadata();
                if (primaryKeyMetadataFromField != null)
                {
                    if (hasPrimaryKey)
                        throw new MojoExecutionException(MessageFormat.format("There are more than 1 primary keys in table \"{0}\" (class = {1})", tableName, entityClassName));

                    hasPrimaryKey = true;
                    primaryKeyMetadata = primaryKeyMetadataFromField;
                }

                // Column metadata.
                ColumnMetadata columnMetadata = columnMetadataWrapper.getColumnMetadata();
                columnMetadatas.add(columnMetadata);

                // Set auto increment info.
                if (columnMetadata.getAutoIncrement() != null)
                {
                    if (hasAutoIncrement)
                        throw new MojoExecutionException(MessageFormat.format("There are more than 1 auto increment columns in table \"{0}\" (class = {1})", tableName, entityClassName));

                    hasAutoIncrement = true;
                }

                // Keys.
                List<KeyBuilderInfo> keyBuilderInfos = columnMetadataWrapper.getKeyBuilderInfos();
                for (KeyBuilderInfo keyBuilderInfo : keyBuilderInfos)
                {
                    if (!keyMetadataMap.containsKey(keyBuilderInfo.getKey()))
                        keyMetadataMap.put(keyBuilderInfo.getKey(), new ArrayList<>());

                    keyMetadataMap.get(keyBuilderInfo.getKey()).add(keyBuilderInfo);
                }
            }
        }

        if (columnMetadatas.isEmpty())
            throw new MojoExecutionException("There is no column definition in table \"" + tableName + "\" (class = " + entityClassName + ").");

        if (primaryKeyMetadata == null)
            throw new MojoExecutionException("There is no primary key definition in table \"" + tableName + "\" (class = " + entityClassName + ").");

        TableSchemaInfo tableSchemaInfo = new TableSchemaInfo();
        tableSchemaInfo.setName(tableName);
        tableSchemaInfo.setComment(tableMetadata.getComment());
        tableSchemaInfo.setEngine(tableMetadata.getEngine());
        tableSchemaInfo.setColumnMetadatas(columnMetadatas);
        tableSchemaInfo.setPrimaryKeyMetadata(primaryKeyMetadata);
        tableSchemaInfo.setKeyMetadatas(toKeyMetadatas(keyMetadataMap));
        return tableSchemaInfo;
    }

    private List<KeyMetadata> toKeyMetadatas(HashMap<String, List<KeyBuilderInfo>> keyMetadataMap) throws MojoExecutionException
    {
        List<KeyMetadata> keyMetadatas = new ArrayList<>();

        for (String keyName : keyMetadataMap.keySet())
        {
            List<KeyBuilderInfo> keyBuilderInfos = keyMetadataMap.get(keyName);
            validateKeyOrders(keyBuilderInfos);

            keyBuilderInfos.sort(Comparator.comparingInt(KeyBuilderInfo::getOrder));
            List<String> columns = keyBuilderInfos.stream().map(x -> x.getColumn().getName()).toList();

            KeyMetadata keyMetadata = KeyMetadata.builder()
                .name(keyName)
                .columns(columns)
                .build();

            keyMetadatas.add(keyMetadata);
        }

        return keyMetadatas;
    }

    private void validateKeyOrders(List<KeyBuilderInfo> keyBuilderInfos) throws MojoExecutionException
    {
        HashSet<Integer> keyIndexOrders = new HashSet<>();

        for (KeyBuilderInfo keyBuilderInfo : keyBuilderInfos)
        {
            int order = keyBuilderInfo.getOrder();
            if (keyIndexOrders.contains(order))
                throw new MojoExecutionException("Columns of the same key must have different order. Field = " + keyBuilderInfo.getField().getName() + ", order = " + order + ", class = " + entityClassName + ".");

            keyIndexOrders.add(order);
        }
    }

    private String getTableName() throws MojoExecutionException
    {
        String tableName;
        if (table == null)
            tableName = entityClassSimpleName;
        else
        {
            try
            {
                String tableNameInAnnotation = table.name();
                String candidateTableNameBeforeConversion = tableNameInAnnotation.isEmpty() ? entityClassSimpleName : tableNameInAnnotation;
                tableName = NamingConverter.applyConvention(candidateTableNameBeforeConversion, namingConvention);
            }
            catch (IllegalArgumentException e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

        return tableName;
    }

    private TableMetadata toTableMetadata()
    {
        TableMetadata tableMetadata = new TableMetadata();

        if (table == null)
        {
            tableMetadata.setName(entityClassSimpleName);
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
