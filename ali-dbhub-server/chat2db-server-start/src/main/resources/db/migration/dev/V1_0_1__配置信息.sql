CREATE TABLE IF NOT EXISTS `system_config` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `code` varchar(32) NOT NULL COMMENT '配置项编码',
    `content` varchar(256) DEFAULT NULL COMMENT '配置项内容',
    `summary` varchar(256) DEFAULT NULL COMMENT '配置项说明',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='配置中心表'
;
create UNIQUE INDEX uk_code on system_config(code) ;