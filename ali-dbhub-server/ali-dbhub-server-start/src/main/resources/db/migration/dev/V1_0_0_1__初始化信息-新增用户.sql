CREATE TABLE IF NOT EXISTS `dbhub_user` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `user_name` varchar(32) NOT NULL COMMENT '用户名',
    `password` varchar(256) DEFAULT NULL COMMENT '密码',
    `nick_name` varchar(256) DEFAULT NULL COMMENT '昵称',
    `email` varchar(256) DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='数据源连接表'
;

INSERT INTO `dbhub_user` (`user_name`,`password`,`nick_name`) VALUES ('dbhub','$2a$10$yElafjDHPoPHSaCo6cjJGuWmtXWNVz/cOOOtDg99eNfvUfalzfane','管理员');