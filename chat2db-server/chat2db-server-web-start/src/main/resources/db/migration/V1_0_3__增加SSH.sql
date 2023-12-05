ALTER TABLE `data_source` ADD COLUMN `host` varchar(128) NULL COMMENT 'host地址';
ALTER TABLE `data_source` ADD COLUMN `port` varchar(128) NULL COMMENT '端口';
ALTER TABLE `data_source` ADD COLUMN `ssh` varchar(1024) NULL COMMENT 'ssh配置信息json';
ALTER TABLE `data_source` ADD COLUMN `ssl` varchar(1024) NULL COMMENT 'ssl配置信息json';
ALTER TABLE `data_source` ADD COLUMN `sid` varchar(32) NULL COMMENT 'sid';
ALTER TABLE `data_source` ADD COLUMN `driver` varchar(128) NULL COMMENT '驱动信息';
ALTER TABLE `data_source` ADD COLUMN `jdbc` varchar(128) NULL COMMENT 'jdbc版本';
ALTER TABLE `data_source` ADD COLUMN `extend_info` varchar(4096) NULL COMMENT '自定义扩展字段json';
create INDEX idx_user_id on data_source(user_id) ;