CREATE TABLE IF NOT EXISTS `environment`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`       datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`   bigint(20) unsigned NOT NULL COMMENT '创建人用户id',
    `modified_user_id` bigint(20) unsigned NOT NULL COMMENT '修改人用户id',
    `name`             varchar(128)                 DEFAULT NOT NULL COMMENT '环境名称',
    `short_name`       varchar(128)                 DEFAULT NULL COMMENT '环境缩写',
    `style`            varchar(32)                  DEFAULT NULL COMMENT '样式类型',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据库连接环境'
;

INSERT INTO `environment`
(`id`, `create_user_id`, `modified_user_id`, `name`, `short_name`, `style`)
VALUES (1, 1, 1, '线上环境', '线上', 'RELEASE');
INSERT INTO `environment`
(`id`, `create_user_id`, `modified_user_id`, `name`, `short_name`, `style`)
VALUES (2, 1, 1, '测试环境', '测试', 'TEST');

ALTER TABLE `data_source`
    ADD COLUMN `environment_id` bigint(20) unsigned NOT NULL DEFAULT 2 COMMENT '环境id';

ALTER TABLE `dbhub_user`
    ADD COLUMN `role_code` varchar(32) DEFAULT NULL COMMENT '角色编码';

ALTER TABLE `dbhub_user`
    ADD `status` varchar(32) NOT NULL DEFAULT 'VALID' COMMENT '用户状态';

update dbhub_user
set role_code= 'DESKTOP'
where id = 1;

CREATE TABLE IF NOT EXISTS `team`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`       datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`   bigint(20) unsigned NOT NULL COMMENT '创建人用户id',
    `modified_user_id` bigint(20) unsigned NOT NULL COMMENT '修改人用户id',
    `code`             varchar(128)                 DEFAULT NOT NULL COMMENT '团队编码',
    `name`             varchar(512)                 DEFAULT NULL COMMENT '团队名称',
    `status`           varchar(32)         NOT NULL DEFAULT 'VALID' COMMENT '团队状态',
    `role_code`        varchar(32)                  DEFAULT NULL COMMENT '角色编码',
    `description`      text                         DEFAULT NULL COMMENT '团队描述',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='团队'
;

create UNIQUE INDEX uk_team_code on team (code);


CREATE TABLE IF NOT EXISTS `team_dbhub_user`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`       datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`     datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`   bigint(20) unsigned NOT NULL COMMENT '创建人用户id',
    `modified_user_id` bigint(20) unsigned NOT NULL COMMENT '修改人用户id',
    `team_id`          bigint(20) unsigned NOT NULL COMMENT '团队id',
    `dbhub_user_id`    bigint(20) unsigned NOT NULL COMMENT '用户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户团队表'
;

create INDEX idx_team_dbhub_user_team_id on team_dbhub_user (`team_id`);
create INDEX idx_team_dbhub_user_dbhub_user_id on team_dbhub_user (`dbhub_user_id`);

CREATE TABLE IF NOT EXISTS `data_source_access`
(
    `id`                 bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`         datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`       datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user_id`     bigint(20) unsigned NOT NULL COMMENT '创建人用户id',
    `modified_user_id`   bigint(20) unsigned NOT NULL COMMENT '修改人用户id',
    `data_source_id`     bigint(20) unsigned NOT NULL COMMENT '数据源id',
    `access_object_type` varchar(32)         NOT NULL COMMENT '授权类型',
    `access_object_id`   bigint(20) unsigned NOT NULL COMMENT '授权id,根据类型区分是用户还是团队',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据源授权'
;

create INDEX data_source_access_data_source_id on data_source_access (`data_source_id`);
create INDEX data_source_access_access_object_id on data_source_access (`access_object_type`, `access_object_id`);
