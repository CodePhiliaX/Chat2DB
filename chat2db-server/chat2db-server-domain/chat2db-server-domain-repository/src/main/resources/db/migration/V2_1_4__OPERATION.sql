ALTER TABLE `operation_log`
    ADD COLUMN  `schema_name` varchar(256) COMMENT 'schema名称';
create INDEX idx_op_data_source_id on operation_log(data_source_id) ;