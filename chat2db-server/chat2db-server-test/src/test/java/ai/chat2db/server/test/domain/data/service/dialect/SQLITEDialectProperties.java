
package ai.chat2db.server.test.domain.data.service.dialect;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author jipengfei
 * @version : SQLITEDialectProperties.java
 */
@Component
public class SQLITEDialectProperties implements DialectProperties{
    @Override
    public String getDbType() {
        return "SQLITE";
    }

    @Override
    public String getUrl() {
        return "jdbc:sqlite:identifier.sqlite";
    }

    @Override
    public String getErrorUrl() {
        return null;
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
        return "main";
    }

    @Override
    public String getCrateTableSql(String tableName) {
        return "CREATE TABLE `" + tableName + "`\n\t"
            + "(\n\t"
            + "    `id`     bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT '主键自增',\n\t"
            + "    `date`   datetime(3)                          not null COMMENT '日期',\n\t"
            + "    `number` bigint COMMENT '长整型',\n\t"
            + "    `string` VARCHAR(100) default 'DATA' COMMENT '名字',\n\t"
            + "    index " + tableName + "_idx_date (date desc) comment '日期索引',\n\t"
            + "    unique " + tableName + "_uk_number (number) comment '唯一索引',\n\t"
            + "    index " + tableName + "_idx_number_string (number, date) comment '联合索引'\n\t"
            + ") COMMENT ='测试表';";
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
        return "select *\n"
            + "from " + tableName + "_notfound;";
    }

    @Override
    public String toCase(String string) {
        return StringUtils.toRootLowerCase(string);
    }
}