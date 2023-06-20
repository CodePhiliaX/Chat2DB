/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.oracle;

import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;

/**
 * @author jipengfei
 * @version : OracleMetaSchemaSupport.java
 */
public class OracleMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {


    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.ORACLE;
    }

//    @Override
//    public List<String> databases() {
//        return super.schemas(null);
//    }
//
//    @Override
//    public List<String> schemas(String databaseName) {
//        return Lists.newArrayList();
//    }

    @Override
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        String sql = "select dbms_metadata.get_ddl('TABLE','"+tableName+"') as sql from dual,"
            + "user_tables where table_name = '" + tableName + "'";
        return SQLExecutor.getInstance().executeSql(sql, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("sql");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null;
        });
    }
}