/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect;

import java.util.List;

import com.alibaba.dbhub.server.domain.support.dialect.common.SQLParam;
import com.alibaba.dbhub.server.domain.support.dialect.common.SQLType;
import com.alibaba.dbhub.server.domain.support.model.Function;
import com.alibaba.dbhub.server.domain.support.model.Procedure;
import com.alibaba.dbhub.server.domain.support.model.Table;
import com.alibaba.dbhub.server.domain.support.model.TableColumn;
import com.alibaba.dbhub.server.domain.support.model.TableIndex;
import com.alibaba.dbhub.server.domain.support.model.Trigger;
import com.alibaba.dbhub.server.domain.support.sql.SQLExecutor;
import com.alibaba.druid.sql.parser.DbhubSQLParserUtils;

/**
 * @author jipengfei
 * @version : BaseMetaSchema.java
 */
public abstract class BaseMetaSchema implements MetaSchema {

    @Override
    public List<String> databases() {
        return SQLExecutor.getInstance().databases();
    }

    public String getSQL(SQLType sqlType, SQLParam params) {
        switch (sqlType) {
            case CREATE_DATABASE:
                return "CREATE DATABASE " + params.getDatabaseName();
            case DROP_DATABASE:
                return "DROP DATABASE " + params.getDatabaseName();
            case MODIFY_DATABASE:
                return "ALTER DATABASE " + params.getDatabaseName() + " RENAME TO " + params.getNewDatabaseName();
            case CREATE_SCHEMA:
                return "CREATE SCHEMA " + params.getSchemaName();
            case DROP_SCHEMA:
                return "DROP SCHEMA " + params.getSchemaName();
            case MODIFY_SCHEMA:
                return "ALTER SCHEMA " + params.getSchemaName() + " RENAME TO " + params.getNewSchemaName();
        }
        return null;
    }

    @Override
    public void modifyDatabase(String databaseName, String newDatabaseName) {
        String sql = getSQL(SQLType.MODIFY_DATABASE, SQLParam.builder().databaseName(databaseName)
            .newDatabaseName(newDatabaseName).build());
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public void createDatabase(String databaseName) {
        String sql = getSQL(SQLType.CREATE_DATABASE, SQLParam.builder().databaseName(databaseName).build());
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public void dropDatabase(String databaseName) {
        String sql = getSQL(SQLType.DROP_DATABASE, SQLParam.builder().databaseName(databaseName).build());
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public void createSchema(String databaseName, String schemaName) {
        String sql = getSQL(SQLType.CREATE_SCHEMA, SQLParam.builder().databaseName(databaseName).schemaName(schemaName)
            .build());
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public void dropSchema(String databaseName, String schemaName) {
        String sql = getSQL(SQLType.DROP_SCHEMA, SQLParam.builder().databaseName(databaseName).schemaName(schemaName)
            .build());
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public void modifySchema(String databaseName, String schemaName, String newSchemaName) {
        String sql = getSQL(SQLType.MODIFY_SCHEMA, SQLParam.builder().databaseName(databaseName).schemaName(schemaName)
            .newSchemaName(newSchemaName).build());
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public List<String> schemas(String databaseName) {
        return SQLExecutor.getInstance().schemas(databaseName, null);
    }

    @Override
    public String tableDDL(String databaseName, String schemaName, String tableName) {
        return null;
    }

    @Override
    public void dropTable(String databaseName, String schemaName, String tableName) {
        String sql = "drop table " + DbhubSQLParserUtils.format(dbType(),tableName);
        SQLExecutor.getInstance().executeSql(sql, resultSet -> null);
    }

    @Override
    public List<Table> tables(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().tables(databaseName, schemaName, tableName, new String[] {"TABLE"});
    }

    @Override
    public List<? extends Table> views(String databaseName, String schemaName) {
        return SQLExecutor.getInstance().tables(databaseName, schemaName, null, new String[] {"VIEW"});
    }

    @Override
    public List<Function> functions(String databaseName, String schemaName) {
        return SQLExecutor.getInstance().functions(databaseName, schemaName);
    }

    @Override
    public List<Trigger> triggers(String databaseName, String schemaName) {
        return null;
    }

    @Override
    public List<Procedure> procedures(String databaseName, String schemaName) {
        return SQLExecutor.getInstance().procedures(databaseName, schemaName);
    }

    @Override
    public List<? extends TableColumn> columns(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().columns(databaseName, schemaName, tableName, null);
    }

    @Override
    public List<? extends TableColumn> columns(String databaseName, String schemaName, String tableName,
        String columnName) {
        return SQLExecutor.getInstance().columns(databaseName, schemaName, tableName, columnName);
    }

    @Override
    public List<? extends TableIndex> indexes(String databaseName, String schemaName, String tableName) {
        return SQLExecutor.getInstance().indexes(databaseName, schemaName, tableName);
    }
}