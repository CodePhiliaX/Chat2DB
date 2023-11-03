package ai.chat2db.plugin.dm.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("DM");
        dbConfig.setDbType("DM");

        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("DmJdbcDriver18-8.1.2.141.jar");
        driverConfig.setJdbcDriverClass("dm.jdbc.driver.DmDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/DmJdbcDriver18-8.1.2.141.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
