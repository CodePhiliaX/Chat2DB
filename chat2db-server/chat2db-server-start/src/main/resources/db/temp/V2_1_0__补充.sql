ALTER TABLE `operation_saved`
    modify COLUMN  `user_id` bigint(20) unsigned NOT NULL DEFAULT 1 COMMENT '用户id';

update operation_saved
set user_id= 1;