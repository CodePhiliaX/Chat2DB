package ai.chat2db.plugin.h2.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("H2");
        dbConfig.setDbType("H2");

        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("h2-2.1.214.jar");
        driverConfig.setJdbcDriverClass("org.h2.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/h2-2.1.214.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
