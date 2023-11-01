package ai.chat2db.plugin.sqlite.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("SQLite");
        dbConfig.setDbType("SQLITE");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("sqlite-jdbc-3.39.3.0.jar");
        driverConfig.setJdbcDriverClass("org.sqlite.JDBC");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/sqlite-jdbc-3.39.3.0.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
