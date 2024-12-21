package stark.coderaider.fluentschema.entities;

import stark.coderaider.fluentschema.commons.NamingConvention;
import stark.coderaider.fluentschema.commons.annotations.Column;
import stark.coderaider.fluentschema.commons.annotations.Key;
import stark.coderaider.fluentschema.commons.annotations.PrimaryKey;
import stark.coderaider.fluentschema.commons.annotations.Table;

@Table(namingConvention = NamingConvention.LOWER_CASE_WITH_UNDERSCORE, comment = "History (tracking) of table schema change produced by fluent schema.")
public class SchemaSnapshotHistory
{
    @PrimaryKey
    private long id;

    @Key(name = "idx_schema_snapshot_name")
    @Column(nullable = false, unique = true, comment = "ID of the schema snapshot.")
    private String schemaSnapshotName;

    @Column(nullable = false, type = "VARCHAR(200)", comment = "Version of fluent schema which generates the schema snapshot history.")
    private String fluentSchemaVersion;
}
