ALTER TABLE table_vector_mapping MODIFY COLUMN status VARCHAR(64);

ALTER TABLE operation_saved ALTER COLUMN data_source_id SET DEFAULT 0;

ALTER TABLE operation_saved  ALTER COLUMN type SET default '';