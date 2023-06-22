package ai.chat2db.server.test.domain.data.service.dialect;

import java.util.Date;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * h2
 *
 * @author Jiaju Zhuang
 */
@Component
public class H2DialectProperties implements DialectProperties {

    @Override
    public String getDbType() {
        return "H2";
    }

    @Override
    public String getUrl() {
        return "jdbc:h2:~/.dbhub/db/ali_dbhub_dev;MODE=MYSQL";
    }

    @Override
    public String getErrorUrl() {
        return "jdbc:h2:tcp://error:8084/error";
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getDatabaseName() {
        return "ALI_DBHUB_DEV";
    }

    @Override
    public String getCrateTableSql(String tableName) {
        // TODO druid有sql解析的bug
        String sql = "CREATE TABLE `" + tableName + "`\n\t"
            + "(\n\t"
            + "    `id`     bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键自增',\n\t"
            + "    `date`   datetime                          not null COMMENT '日期',\n\t"
            + "    `number` bigint COMMENT '长整型',\n\t"
            + "    `string` VARCHAR(100) default 'DATA' COMMENT '名字'\n\t"
            + ");\n\t";
        sql += "comment on table " + tableName + " is '测试表';\n\t";
        sql += "create index " + tableName + "_idx_date on " + tableName + "(DATE desc);\n\t";
        sql += "comment on index " + tableName + "_idx_date is '日期索引';\n\t";
        sql += "create unique index " + tableName + "_uk_number   on " + tableName + "(NUMBER);\n\t";
        sql += "comment on index " + tableName + "_uk_number is '唯一索引';\n\t";
        sql += "create index " + tableName + "_idx_number_string   on " + tableName + "(NUMBER, DATE);\n\t";
        sql += "comment on index " + tableName + "_idx_number_string is '联合索引';\n\t";
        return sql;
    }

    @Override
    public String getDropTableSql(String tableName) {
        return "drop table " + tableName + ";";
    }

    @Override
    public String getInsertSql(String tableName, Date date, Long number, String string) {
        return "INSERT INTO `" + tableName + "` (date,number,string) VALUES ('" + DateUtil.format(date,
            DatePattern.NORM_DATETIME_MS_FORMAT) + "','" + number + "','" + string + "');";
    }

    @Override
    public String getSelectSqlById(String tableName, Long id) {
        return "select *\n\t"
            + "from " + tableName + "\n\t"
            + "where `id` = '" + id + "';";
    }

    @Override
    public String getTableNotFoundSqlById(String tableName) {
        return "select *\n\t"
            + "from " + tableName + "_notfound;";
    }

    @Override
    public String toCase(String string) {
        return StringUtils.toRootUpperCase(string);
    }
}
