package ai.chat2db.plugin.db2.constant;

/**
 * @author: zgq
 * @date: 2024年03月16日 10:11
 */
public class SQLConstant {

    public static final String TABLE_DDL_FUNCTION_SQL
            = """
              CREATE OR REPLACE FUNCTION generate_table_ddl(schema_name VARCHAR(128), table_name VARCHAR(128))
                  RETURNS CLOB
                  LANGUAGE SQL
              BEGIN
                  DECLARE ddl CLOB;

                  -- 获取表的注释信息
                  DECLARE table_remarks CLOB;
                  SELECT REMARKS
                  INTO table_remarks
                  FROM SYSCAT.TABLES
                  WHERE TABSCHEMA = schema_name
                    AND TABNAME = table_name;

                  -- 拼接表的创建语句
                  SET ddl = 'CREATE TABLE ' || table_name || ' (';

                  -- 获取表的字段信息并拼接到DDL语句中
                  FOR col_info AS
                      SELECT COLNAME, TYPENAME, LENGTH, SCALE, NULLS, DEFAULT as default, REMARKS
                      FROM SYSCAT.COLUMNS
                      WHERE TABSCHEMA = schema_name
                        AND TABNAME = table_name
                      ORDER BY COLNO
                      DO
                          SET ddl = ddl || col_info.COLNAME || ' ';
                          IF col_info.TYPENAME = 'INTEGER' THEN
                              SET ddl = ddl || col_info.TYPENAME;
                          ELSE
                              SET ddl = ddl || col_info.TYPENAME;
                              IF col_info.LENGTH IS NOT NULL THEN
                                  SET ddl = ddl || '(' || col_info.LENGTH;
                                  IF col_info.TYPENAME != 'VARCHAR' AND col_info.SCALE IS NOT NULL THEN
                                      SET ddl = ddl || ',' || col_info.SCALE;
                                  END IF;
                                  SET ddl = ddl || ')';
                              END IF;
                          END IF;
                          IF col_info.NULLS = 'N' THEN
                              SET ddl = ddl || ' NOT NULL';
                          END IF;
                          IF col_info.default IS NOT NULL THEN
                              SET ddl = ddl || ' DEFAULT ' || col_info.default;
                          END IF;
                          SET ddl = ddl || ','; -- 添加字段定义结束符
                      END FOR;

                  -- 删除最后一个逗号
                  SET ddl = LEFT(ddl, LENGTH(ddl) - 1);
                  SET ddl = ddl || ');';

                  -- 添加表的注释
                  IF table_remarks IS NOT NULL THEN
                      SET ddl = ddl || 'comment on table ' || table_name || ' is ''' || table_remarks || ''';';
                  END IF;

                  for column as
                      SELECT COLNAME, REMARKS
                      FROM SYSCAT.COLUMNS
                      WHERE TABSCHEMA = schema_name
                        AND TABNAME = table_name
                      ORDER BY COLNO
                      do
                          if column.REMARKS is not null then
                              set ddl = ddl || 'comment on column ' || table_name || '.' || column.COLNAME || ' is ''' ||
                                        column.REMARKS || ''';';
                          end if;
                      end for;

                  -- 获取表的索引信息并拼接到DDL语句中
                  FOR index_info AS
                      SELECT INDNAME, SUBSTR(COLNAMES, 2) AS COLNAMES, UNIQUERULE
                      FROM SYSCAT.INDEXES
                      WHERE TABSCHEMA = schema_name
                        AND TABNAME = table_name
                      DO
                          IF index_info.UNIQUERULE = 'P' THEN
                              SET ddl = ddl || ' ALTER TABLE ' || table_name || ' ADD PRIMARY KEY (' ||
                                        index_info.COLNAMES || ');';
                          ELSEIF index_info.UNIQUERULE = 'U' THEN
                              SET ddl = ddl || ' CREATE UNIQUE INDEX ' || index_info.INDNAME || ' ON ' ||
                                        table_name || ' (' || index_info.COLNAMES || ');';
                          END IF;
                      END FOR;

                  RETURN ddl;
              END;""";
}
