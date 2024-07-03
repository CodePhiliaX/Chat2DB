package ai.chat2db.server.start.test.dialect;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class OracleDialectProperties implements DialectProperties {

    @Override
    public String getDbType() {
        return "ORACLE";
    }

    @Override
    public String getUrl() {
        return "jdbc:oracle:thin:@183.247.151.185:11521:XE";
    }

    @Override
    public String getErrorUrl() {
        return "jdbc:oracle:thin:@183.247.151.185:11521:XE1";
    }

    @Override
    public String getUsername() {
        return "system";
    }

    @Override
    public String getPassword() {
        return "ali_dbhub";
    }

    @Override
    public String getDatabaseName() {
        return "TEST_USER";
    }

    @Override
    public String getCrateTableSql(String tableName) {
        return "CREATE TABLE `" + tableName + "`\n\t"
                + "(\n\t"
                + "    `id`     bigint PRIMARY KEY AUTO_INCREMENT NOT NULL COMMENT 'Primary key auto-increment',\n\t"
                + "    `date`   datetime(3)                          not null COMMENT 'date',\n\t"
                + "    `number` bigint COMMENT 'long integer',\n\t"
                + "    `string` VARCHAR(100) default 'DATA' COMMENT 'name',\n\t"
                + "    index " + tableName + "_idx_date (date desc) comment 'date index',\n\t"
                + "    unique " + tableName + "_uk_number (number) comment 'unique index',\n\t"
                + "    index " + tableName + "_idx_number_string (number, date) comment 'Union index'\n\t"
                + ") COMMENT ='Test table';";
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

    @Override
    public Integer getPort() {
        return 11521;
    }
}
