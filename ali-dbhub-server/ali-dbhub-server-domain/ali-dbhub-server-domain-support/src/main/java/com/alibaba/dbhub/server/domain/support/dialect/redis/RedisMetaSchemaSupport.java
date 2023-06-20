/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.redis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.Table;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : RedisMetaSchemaSupport.java
 */
public class RedisMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {

    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.REDIS;
    }
    @Override
    public String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName) {
        return "";
    }


    @Override
    public List<String> databases() {
        List<String> databases = new ArrayList<>();
        return SQLExecutor.getInstance().executeSql("config get databases", resultSet -> {
            try {
                if (resultSet.next()) {
                    Object count = resultSet.getObject(2);
                    if(StringUtils.isNotBlank(count.toString())) {
                        for (int i = 0; i < Integer.parseInt(count.toString()); i++) {
                            databases.add(String.valueOf(i));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return databases;
        });
    }

    @Override
    public List<Table> tables(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().executeSql("scan 0 MATCH * COUNT 1000", resultSet -> {
            List<Table> tables = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    ArrayList list = (ArrayList)resultSet.getObject(2);
                    for (Object object : list) {
                        Table table = new Table();
                        table.setName(object.toString());
                        tables.add(table);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return tables;
        });
    }
}