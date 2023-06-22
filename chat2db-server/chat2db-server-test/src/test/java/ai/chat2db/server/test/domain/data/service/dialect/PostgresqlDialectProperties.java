/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.test.domain.data.service.dialect;

import java.util.Date;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author jipengfei
 * @version : PgDialectProperties.java, v 0.1 2022年12月13日 21:48 jipengfei Exp $
 */
@Component
public class PostgresqlDialectProperties implements DialectProperties {

    @Override
    public String getDbType() {
        return "POSTGRESQL";
    }

    @Override
    public String getUrl() {
        return "jdbc:postgresql://localhost:5432/ali_dbhub_test";
    }

    @Override
    public String getErrorUrl() {
        return "jdbc:postgresql://error:5432/ali_dbhub";
    }

    @Override
    public String getUsername() {
        return "ali_dbhub";
    }

    @Override
    public String getPassword() {
        return "ali_dbhub";
    }

    @Override
    public String getDatabaseName() {
        return "ali_dbhub_test";
    }

    @Override
    public String getCrateTableSql(String tableName) {
        String sql = "CREATE TABLE " + tableName + "\n"
            + "(\n"
            + "    id     serial\n"
            + "        constraint " + tableName + "_pk primary key,\n"
            + "    date   timestamp,\n"
            + "    number int,\n"
            + "    string varchar(100) default 'DATA'\n"
            + ");\n";
        sql += "comment on table " + tableName + " is '测试表';\n";
        sql += "comment on column " + tableName + ".id is '主键自增';\n";
        sql += "comment on column " + tableName + ".date is '日期';\n";
        sql += "comment on column " + tableName + ".number is '长整型';\n";
        sql += "comment on column " + tableName + ".string is '名字';\n";
        sql += "create index " + tableName + "idx_date on " + tableName + " (date desc);";
        sql += "create unique index " + tableName + "_uk_number on " + tableName + " (number);";
        sql += "create index " + tableName + "_idx_number_string on " + tableName + " (number, date);";
        sql += "comment on index " + tableName + "_uk_number is '日期索引';";
        sql += "comment on index " + tableName + "_uk_number is '唯一索引';";
        sql += "comment on index " + tableName + "_idx_number_string is '联合索引';";
        return sql;
    }

    @Override
    public String getDropTableSql(String tableName) {
        return "drop table " + tableName + ";";
    }

    @Override
    public String getInsertSql(String tableName, Date date, Long number, String string) {
        return "INSERT INTO " + tableName + " (date,number,string) VALUES ('" + DateUtil.format(date,
            DatePattern.NORM_DATETIME_MS_FORMAT) + "','" + number + "','" + string + "');";
    }

    @Override
    public String getSelectSqlById(String tableName, Long id) {
        return "select *\n"
            + "from " + tableName + "\n"
            + "where id = '" + id + "';";
    }

    @Override
    public String getTableNotFoundSqlById(String tableName) {
        return "select *\n"
            + "from " + tableName + "_notfound;";
    }

    @Override
    public String toCase(String string) {
        return StringUtils.toRootLowerCase(string);
    }
}