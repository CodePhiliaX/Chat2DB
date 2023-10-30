package ai.chat2db.plugin.postgresql.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("PostgreSQL");
        dbConfig.setDbType("POSTGRESQL");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("postgresql-42.5.1.jar");
        driverConfig.setJdbcDriverClass("org.postgresql.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/postgresql-42.5.1.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
