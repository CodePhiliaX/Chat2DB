-- 将 TASK 表的 content 字段从 BLOB 改为 CLOB (对应 Java 层从 byte[] 改为 String)
ALTER TABLE `task` MODIFY COLUMN `content` longtext COMMENT 'task content';
