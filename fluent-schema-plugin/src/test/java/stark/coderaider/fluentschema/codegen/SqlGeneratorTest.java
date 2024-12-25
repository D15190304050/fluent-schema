package stark.coderaider.fluentschema.codegen;

import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import stark.coderaider.fluentschema.commons.schemas.SchemaSnapshotBase;
import stark.coderaider.fluentschema.examples.SchemaMigration06;
import stark.coderaider.fluentschema.examples.SchemaMigration07;
import stark.coderaider.fluentschema.test.migrations.EmptyMigration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

public class SqlGeneratorTest
{
    @Test
    public void testGenerateMigrationSql1() throws IOException, URISyntaxException
    {
        EmptyMigration emptyMigration = new EmptyMigration();
        List<SchemaMigrationBase> schemaMigrations = List.of(emptyMigration);

        SqlGenerator sqlGenerator = new SqlGenerator(schemaMigrations);
        String sql = sqlGenerator.generateMigrationSql();

        System.out.println(sql);
    }

    @Test
    public void testGenerateMigrationSql2() throws IOException, URISyntaxException
    {
        EmptyMigration emptyMigration = new EmptyMigration();
        SchemaMigration06 schemaMigration06 = new SchemaMigration06();
        List<SchemaMigrationBase> schemaMigrations = List.of(emptyMigration, schemaMigration06);

        SqlGenerator sqlGenerator = new SqlGenerator(schemaMigrations);
        String sql = sqlGenerator.generateMigrationSql();

        System.out.println(sql);
    }
}