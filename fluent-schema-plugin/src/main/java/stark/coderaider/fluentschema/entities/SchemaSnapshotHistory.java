package stark.coderaider.fluentschema.entities;

import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.*;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE, comment = "History (tracking) of table schema change produced by fluent schema.")
public class SchemaSnapshotHistory
{
    @PrimaryKey
    @AutoIncrement
    private long id;

    @Key(name = "idx_schema_snapshot_name")
    @Column(nullable = false, type = "VARCHAR(200)", unique = true, comment = "ID of the schema snapshot.")
    private String schemaSnapshotName;

    @Column(nullable = false, type = "VARCHAR(200)", comment = "Version of fluent schema which generates the schema snapshot history.")
    private String fluentSchemaVersion;
}
