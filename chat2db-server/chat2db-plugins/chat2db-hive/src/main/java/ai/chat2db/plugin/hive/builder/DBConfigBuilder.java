package ai.chat2db.plugin.hive.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("Hive");
        dbConfig.setDbType("HIVE");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("hive-jdbc-3.1.2-standalone.jar");
        driverConfig.setJdbcDriverClass("org.apache.hive.jdbc.HiveDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/hive-jdbc-3.1.2-standalone.jar"));
        driverConfig.setName(driverConfig.getJdbcDriver() + ":" + driverConfig.getJdbcDriverClass());
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
