CREATE SCHEMA IF NOT EXISTS `tinyurl`;

USE `tinyurl`;
CREATE TABLE IF NOT EXISTS `tiny_url` (
    tiny_url varchar(7) not null unique,
    long_url varchar(255) not null unique,
    created_date timestamp default current_timestamp,
    primary key (tiny_url),
    INDEX `tiny_url_index` (`tiny_url`),
    INDEX `long_url_index` (`long_url`)
    );