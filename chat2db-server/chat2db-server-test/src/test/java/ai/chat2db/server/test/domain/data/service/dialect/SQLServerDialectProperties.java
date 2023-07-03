
package ai.chat2db.server.test.domain.data.service.dialect;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author jipengfei
 * @version : SQLServerDialectProperties.java
 */
@Component
public class SQLServerDialectProperties implements DialectProperties{
    @Override
    public String getDbType() {
        return "SQLSERVER";
    }

    @Override
    public String getUrl() {
        return "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;integratedSecurity=false;Trusted_Connection=yes";
    }

    @Override
    public String getErrorUrl() {
        return "jdbc:sqlserver://localhost:14331;encrypt=true;trustServerCertificate=true;integratedSecurity=true;";
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "Ali_dbhub!";
    }

    @Override
    public String getDatabaseName() {
        return null;
    }

    @Override
    public String getCrateTableSql(String tableName) {
        return "CREATE TABLE [dbo].["+tableName+"] ( [id] bigint NOT NULL, [date] datetime NOT NULL, [String] varchar(1) NOT NULL, [number] bigint NULL);CREATE UNIQUE CLUSTERED INDEX [id] ON [dbo].[table_name] ( [id] ASC);CREATE NONCLUSTERED INDEX [table_name_date_index] ON [dbo].[table_name] ( [date] ASC);CREATE NONCLUSTERED INDEX [table_name_String_index] ON [dbo].[table_name] ( [String] ASC);CREATE UNIQUE NONCLUSTERED INDEX [table_name_pk] ON [dbo].[table_name] ( [number] ASC);EXEC sp_addextendedproperty @name=N'MS_Description', @value=N'mmm', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'table_name', @level2type=N'COLUMN', @level2name=N'id';EXEC sp_addextendedproperty @name=N'MS_Description', @value=N'mmm', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'table_name', @level2type=N'COLUMN', @level2name=N'date';EXEC sp_addextendedproperty @name=N'MS_Description', @value=N'mmm', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'table_name', @level2type=N'COLUMN', @level2name=N'String';EXEC sp_addextendedproperty @name=N'MS_Description', @value=N'mmm', @level0type=N'SCHEMA', @level0name=N'dbo', @level1type=N'TABLE', @level1name=N'table_name', @level2type=N'COLUMN', @level2name=N'number';";
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
        return "select *\n\t"
            + "from " + tableName + "\n\t"
            + "where `id` = '" + id + "';";
    }

    @Override
    public String getTableNotFoundSqlById(String tableName) {
        return "select *\n\t"
            + "from " + tableName + "_not_find ;";
    }

    @Override
    public String toCase(String string) {
         return StringUtils.toRootLowerCase(string);
    }
}