CREATE TABLE IF NOT EXISTS `data_source_sort`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`     datetime            NOT NULL COMMENT '创建时间',
    `gmt_modified`   datetime            NOT NULL COMMENT '修改时间',
    `user_id`        bigint(20) unsigned NOT NULL COMMENT '用户ID',
    `data_source_id` bigint(20) unsigned NOT NULL COMMENT '数据源ID',
    `sort`           int                 NOT NULL COMMENT '排序',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_data_source_sort` (`user_id`, `data_source_id`),
    KEY `idx_data_source_sort_user` (`user_id`, `sort`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '数据源连接用户排序表';
