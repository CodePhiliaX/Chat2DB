/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.mysql;

import java.sql.SQLException;

import javax.validation.constraints.NotEmpty;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;
import com.alibaba.druid.sql.parser.DbhubSQLParserUtils;

import lombok.extern.slf4j.Slf4j;

import static com.alibaba.druid.sql.parser.DbhubSQLParserUtils.format;

/**
 * @author jipengfei
 * @version : MysqlMetaSchemaSupport.java, v 0.1 2022年12月14日 22:44 jipengfei Exp $
 */
@Slf4j
public class MysqlMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {

    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.MYSQL;
    }

    @Override
    public String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName) {
        String sql = "SHOW CREATE TABLE " + format(dbType(), databaseName) + "."
            + format(dbType(), tableName);
        return SQLExecutor.getInstance().executeSql(sql, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("Create Table");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

}