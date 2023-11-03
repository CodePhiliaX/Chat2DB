package ai.chat2db.plugin.sqlserver.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class SqlServerDBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("SQLServer");
        dbConfig.setDbType("SQLSERVER");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("mssql-jdbc-11.2.1.jre17.jar");
        driverConfig.setJdbcDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/mssql-jdbc-11.2.1.jre17.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
