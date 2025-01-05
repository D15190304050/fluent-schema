package stark.coderaider.fluentschema.codegen;

import org.junit.Test;
import stark.coderaider.fluentschema.commons.schemas.SchemaMigrationBase;
import stark.coderaider.fluentschema.examples.SchemaMigration05;
import stark.coderaider.fluentschema.examples.SchemaMigration06;
import stark.coderaider.fluentschema.test.migrations.EmptyMigration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class SqlGeneratorTest
{
    @Test
    public void testGenerateForwardMigrationSql1() throws IOException, URISyntaxException
    {
        EmptyMigration emptyMigration = new EmptyMigration();
        List<SchemaMigrationBase> schemaMigrations = List.of(emptyMigration);

        SqlGenerator sqlGenerator = new SqlGenerator(schemaMigrations);
        String sql = sqlGenerator.generateForwardMigrationSql();

        System.out.println(sql);
    }

    @Test
    public void testGenerateForwardMigrationSql2() throws IOException, URISyntaxException
    {
        EmptyMigration emptyMigration = new EmptyMigration();
        SchemaMigration06 schemaMigration = new SchemaMigration06();
        List<SchemaMigrationBase> schemaMigrations = List.of(emptyMigration, schemaMigration);

        SqlGenerator sqlGenerator = new SqlGenerator(schemaMigrations);
        String sql = sqlGenerator.generateForwardMigrationSql();

        System.out.println(sql);
    }

    @Test
    public void testGenerateForwardMigrationSql3() throws IOException, URISyntaxException
    {
        EmptyMigration emptyMigration = new EmptyMigration();
        SchemaMigration05 schemaMigration = new SchemaMigration05();
        List<SchemaMigrationBase> schemaMigrations = List.of(emptyMigration, schemaMigration);

        SqlGenerator sqlGenerator = new SqlGenerator(schemaMigrations);
        String sql = sqlGenerator.generateForwardMigrationSql();

        System.out.println(sql);
    }
}