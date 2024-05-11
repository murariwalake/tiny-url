CREATE SCHEMA IF NOT EXISTS `tinyurl`;

USE `tinyurl`;
CREATE TABLE IF NOT EXISTS `tiny_url` (
    id bigint not null auto_increment,
    long_url varchar(255) not null unique,
    tiny_url varchar(7) not null unique,
    created_date timestamp default current_timestamp,
    primary key (id),
    INDEX `tiny_url_index` (`tiny_url`),
    INDEX `long_url_index` (`long_url`)
    );