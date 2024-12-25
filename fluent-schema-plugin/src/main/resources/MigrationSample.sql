CREATE TABLE IF NOT EXISTS `__EFMigrationsHistory`
(
    `MigrationId`    varchar(150) NOT NULL,
    `ProductVersion` varchar(32)  NOT NULL,
    PRIMARY KEY (`MigrationId`)
);

START TRANSACTION;

CREATE TABLE `BlogInfo`
(
    `BlogId`   int          NOT NULL AUTO_INCREMENT,
    `BlogName` varchar(200) NOT NULL,
    PRIMARY KEY (`BlogId`)
);

CREATE TABLE `Posts`
(
    `PostId`     int          NOT NULL AUTO_INCREMENT,
    `Title`      varchar(200) NOT NULL,
    `Content`    longtext     NOT NULL,
    `BlogId`     int          NOT NULL,
    `AuthorName` longtext     NOT NULL,
    PRIMARY KEY (`PostId`),
    CONSTRAINT `FK_Posts_BlogInfo_BlogId` FOREIGN KEY (`BlogId`) REFERENCES `BlogInfo` (`BlogId`) ON DELETE CASCADE
);

CREATE INDEX `IX_Posts_BlogId` ON `Posts` (`BlogId`);

INSERT INTO `__EFMigrationsHistory` (`MigrationId`, `ProductVersion`)
VALUES ('20241225004707_Init', '8.0.8');

COMMIT;

