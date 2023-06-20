/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.dm;

import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;
import com.alibaba.dbhub.server.domain.support.util.SqlUtils;

/**
 * @author jipengfei
 * @version : DMMetaSchemaSupport.java
 */
public class DMMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {

    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.DM;
    }

    @Override
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        String selectObjectDDLSQL = String.format(
            "select dbms_metadata.get_ddl(%s, %s, %s) AS \"sql\" from dual",
            SqlUtils.formatSQLString("TABLE"), SqlUtils.formatSQLString(tableName),
            SqlUtils.formatSQLString(schemaName));
        return SQLExecutor.getInstance().executeSql(selectObjectDDLSQL, resultSet -> {
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