package com.alibaba.druid.sql.parser;

import ai.chat2db.server.domain.support.enums.DbTypeEnum;

/**
 * 临时的sql 解析工具类
 * 已经让druid改了 但是没上线
 *
 * @author 是仪
 */
public class DbhubSQLParserUtils extends SQLParserUtils {


    public static String format(DbTypeEnum dbType, String tableName) {
        if (DbTypeEnum.MYSQL.equals(dbType)) {
            return "`" + tableName + "`";
        } else if (DbTypeEnum.ORACLE.equals(dbType)) {
            return "\"" + tableName + "\"";
        } else if (DbTypeEnum.POSTGRESQL.equals(dbType)) {
            return "\"" + tableName + "\"";
        } else if (DbTypeEnum.SQLITE.equals(dbType)) {
            return "\"" + tableName + "\"";
        } else if (DbTypeEnum.SQLSERVER.equals(dbType)) {
            return "[" + tableName + "]";
        } else if (DbTypeEnum.H2.equals(dbType)) {
            return "\"" + tableName + "\"";
        } else {
            return "\"" + tableName + "\"";
        }
    }

    public static String format(String dbType, String tableName) {
        if (DbTypeEnum.MYSQL.getCode().equalsIgnoreCase(dbType)) {
            return "`" + tableName + "`";
        } else if (DbTypeEnum.ORACLE.getCode().equalsIgnoreCase(dbType)) {
            return "\"" + tableName + "\"";
        } else if (DbTypeEnum.POSTGRESQL.getCode().equalsIgnoreCase(dbType)) {
            return "\"" + tableName + "\"";
        } else if (DbTypeEnum.SQLITE.getCode().equalsIgnoreCase(dbType)) {
            return "\"" + tableName + "\"";
        } else if (DbTypeEnum.SQLSERVER.getCode().equalsIgnoreCase(dbType)) {
            return "[" + tableName + "]";
        } else if (DbTypeEnum.H2.getCode().equalsIgnoreCase(dbType)) {
            return "\"" + tableName + "\"";
        } else {
            return "\"" + tableName + "\"";
        }
    }
}
