/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.sqlite;

import java.sql.SQLException;
import java.util.List;

import ai.chat2db.server.domain.support.dialect.BaseMetaSchema;
import ai.chat2db.server.domain.support.dialect.MetaSchema;
import ai.chat2db.server.domain.support.enums.DbTypeEnum;
import ai.chat2db.server.domain.support.sql.SQLExecutor;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jipengfei
 * @version : SqlserverMetaSchemaSupport.java
 */
@Slf4j
public class SQLiteMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {

    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.SQLITE;
    }

    @Override
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        String sql = "SELECT sql FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
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
    @Override
    public List<String> databases() {
        return Lists.newArrayList("main");
    }

    @Override
    public List<String> schemas(String databaseName) {
        return Lists.newArrayList();
    }
}