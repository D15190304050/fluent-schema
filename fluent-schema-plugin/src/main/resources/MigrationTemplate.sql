DROP PROCEDURE IF EXISTS #{sp_alter_tables};

DELIMITER $$

CREATE PROCEDURE #{sp_alter_tables}(IN snapshot_name VARCHAR(150))
BEGIN
    DECLARE recordExists INT DEFAULT 0;

    SET AUTOCOMMIT = 0;

    START TRANSACTION;

    SELECT COUNT(*) INTO recordExists
    FROM `schema_snapshot_history`
    WHERE `schema_snapshot_history`.schema_snapshot_name = snapshot_name;

    -- Alter table structures here if there is no record of snapshot history.
    IF recordExists = 0 THEN
        -- Schema changes.
        ALTER TABLE sampledataoflearningmysql ADD COLUMN teacher_name VARCHAR(255);

        -- schema_snapshot_history.
        INSERT INTO `schema_snapshot_history` (schema_snapshot_name, fluent_schema_version)
        VALUES (snapshot_name, '8.0.33'); -- 假设这是 MySQL 版本

    END IF;

    SET AUTOCOMMIT = 1;
END $$

DELIMITER ;

CALL #{sp_alter_tables}(#{snapshotName});