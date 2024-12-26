package stark.coderaider.fluentschema.goals;

import lombok.SneakyThrows;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.util.CollectionUtils;
import stark.coderaider.fluentschema.codegen.SchemaMigrationCodeGenerator;
import stark.coderaider.fluentschema.codegen.SnapshotCodeGenerator;
import stark.coderaider.fluentschema.commons.NamingConverter;
import stark.coderaider.fluentschema.commons.schemas.TableSchemaInfo;
import stark.coderaider.fluentschema.parsing.EntityParser;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.basic.params.OutValue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Add @Execute(phase = LifecyclePhase.COMPILE) to make sure the project will be compiled before the execution of this goa.
// Because this goal relies on the compiled .class files.
@Mojo(name = "generate-schema", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME)
@Execute(phase = LifecyclePhase.COMPILE)
public class GenerateSchema extends GoalBase
{
    public static final SimpleDateFormat DATE_FORMAT;
    public static final String DEFAULT_SCHEMA_NAME = "SchemaSnapshot";

    @Parameter(property = "entityPackage", required = true)
    private String entityPackage;

    private String schemaSnapshotClassName;

    protected List<TableSchemaInfo> newTableSchemaInfos;

    static
    {
        DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    @SneakyThrows
    @Override
    public void execute()
    {
        // Preparation.
        super.prepare();
        prepareSchemaPackage();
        prepareSchemaSnapshotClassName();

        // Get old table schemas.
        OutValue<Boolean> initialized = new OutValue<>();
        List<TableSchemaInfo> oldTableSchemaInfos = getOldTableSchemaInfos(initialized);

        // Get new table schemas.
        newTableSchemaInfos = parseEntitiesForTableSchemas();

        // Write schema migration class.
        String schemaMigrationHistoryClassName = getSchemaMigrationHistoryClassName();
        String codeOfSchemaMigration = SchemaMigrationCodeGenerator.generateSchemaMigration(schemaPackage, schemaMigrationHistoryClassName, newTableSchemaInfos, oldTableSchemaInfos, initialized.getValue());
        writeCodeToClass(schemaMigrationHistoryClassName, codeOfSchemaMigration);

        // Write schema snapshot class.
        String schemaSnapshotClassSimpleName = schemaSnapshotClassName.substring(schemaSnapshotClassName.lastIndexOf('.') + 1);
        String codeOfSchemaSnapshot = SnapshotCodeGenerator.generateSchemaSnapshot(schemaPackage, schemaSnapshotClassSimpleName, newTableSchemaInfos);
        writeCodeToClass(schemaSnapshotClassSimpleName, codeOfSchemaSnapshot);
    }

    private List<TableSchemaInfo> getOldTableSchemaInfos(OutValue<Boolean> initialized) throws MojoExecutionException
    {
        Class<?> schemaSnapshotClass = loadSchemaSnapshotClass();
        List<TableSchemaInfo> oldTableSchemaInfos;
        if (schemaSnapshotClass == null)
        {
            initialized.setValue(false);
            oldTableSchemaInfos = new ArrayList<>();
        }
        else
        {
            initialized.setValue(true);

            String schemaSnapshotClassName = schemaSnapshotClass.getName();
            try
            {
                Constructor<?> constructor = schemaSnapshotClass.getConstructor();
                Object schemaSnapshot = constructor.newInstance();
                Method mBuildSchema = schemaSnapshotClass.getMethod("buildSchema");
                mBuildSchema.invoke(schemaSnapshot);
                Method mGetTableSchemaInfos = schemaSnapshotClass.getMethod("getTableSchemaInfos");
                oldTableSchemaInfos = (List<TableSchemaInfo>) mGetTableSchemaInfos.invoke(schemaSnapshot);
                getLog().info("oldTableSchemaInfos = " + JsonSerializer.serialize(oldTableSchemaInfos));
            }
            catch (NoSuchMethodException e)
            {
                throw new MojoExecutionException("There is no method \"public List<TableSchemaInfo> getTableSchemaInfos()\" in " + schemaSnapshotClassName, e);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new MojoExecutionException("Error instantiating " + schemaSnapshotClassName, e);
            }
        }

        return oldTableSchemaInfos;
    }

    private List<TableSchemaInfo> parseEntitiesForTableSchemas() throws MojoExecutionException
    {
        List<Class<?>> entityClasses = loadEntityClasses();
        if (CollectionUtils.isEmpty(entityClasses))
        {
            getLog().info("No entity classes found.");
            return null;
        }

        List<TableSchemaInfo> newTableSchemaInfos = new ArrayList<>();
        for (Class<?> entityClass : entityClasses)
        {
            EntityParser parser = new EntityParser();
            TableSchemaInfo tableSchemaInfo = parser.parse(entityClass);
            newTableSchemaInfos.add(tableSchemaInfo);
        }

        return newTableSchemaInfos;
    }

    private List<Class<?>> loadEntityClasses() throws MojoExecutionException
    {
        getLog().info("Analyzing sources in directory: " + entityPackage);

        List<File> javaFiles = findClassesInPackage(entityPackage);
        if (javaFiles.isEmpty())
        {
            getLog().warn("No Java files found in the specified package: " + entityPackage);
            throw new MojoExecutionException("No Java files found in the specified package: " + entityPackage);
        }

        try
        {
            return loadCompiledClasses(javaFiles);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error during class loading or compilation", e);
        }
    }

    private Class<?> loadSchemaSnapshotClass() throws MojoExecutionException
    {
        getLog().info("Schema snapshot class name: " + schemaSnapshotClassName);

        List<Class<?>> classesInSchemaPackage = loadClassesInSchemaPackage();
        for (Class<?> schemaSnapshotClass : classesInSchemaPackage)
        {
            if (schemaSnapshotClassName.equals(schemaSnapshotClass.getName()))
                return schemaSnapshotClass;
        }

        return null;
    }

    private void prepareSchemaSnapshotClassName()
    {
        getLog().info("Schema package: " + schemaPackage);
        String dataSourceClassName = NamingConverter.toClassLikeName(dataSourceName);
        schemaSnapshotClassName = schemaPackage + "." + dataSourceClassName + DEFAULT_SCHEMA_NAME;
    }

    private void prepareSchemaPackage() throws MojoExecutionException
    {
        if (schemaPackage == null || schemaPackage.isEmpty())
            schemaPackage = entityPackage.substring(0, entityPackage.lastIndexOf('.')) + ".schemas";

        String schemaPackagePath = sourceDirectory.getAbsolutePath() + File.separator + schemaPackage.replace('.', File.separatorChar);
        File schemaPackageDir = new File(schemaPackagePath);

        // Create the directory of the schema package if it does not exist.
        if (!schemaPackageDir.exists())
        {
            if (!schemaPackageDir.mkdirs())
                throw new MojoExecutionException("Failed to create output directory: " + schemaPackageDir);
            getLog().info("Schema package " + schemaPackage + " created.");
        }
    }

    private String getSchemaMigrationHistoryClassName()
    {
        Date now = new Date();
        String timestamp = DATE_FORMAT.format(now);
        return SCHEMA_MIGRATION_CLASS_NAME_PREFIX + timestamp;
    }

    private String getCodeFilePath(String classSimpleName)
    {
        return sourceDirectory + File.separator + schemaPackage.replace(".", File.separator) + File.separator + classSimpleName + ".java";
    }

    private void writeCodeToClass(String className, String code) throws IOException
    {
        String codeFilePath = getCodeFilePath(className);
        Files.writeString(Path.of(codeFilePath), code, StandardCharsets.UTF_8);
    }
}
