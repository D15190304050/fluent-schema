CREATE TABLE IF NOT EXISTS `schema_snapshot_history`
(
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT,
    `schema_snapshot_name`  VARCHAR(200) NOT NULL UNIQUE COMMENT 'ID of the schema snapshot.',
    `fluent_schema_version` VARCHAR(200) NOT NULL COMMENT 'Version of fluent schema which generates the schema snapshot history.',

    PRIMARY KEY (`id`),
    KEY `idx_schema_snapshot_name` (`schema_snapshot_name`)
)
    ENGINE = InnoDB
    AUTO_INCREMENT = 1
    COMMENT 'History (tracking) of table schema changes produced by fluent schema.';