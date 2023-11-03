package ai.chat2db.plugin.presto.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("Presto");
        dbConfig.setDbType("PRESTO");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("presto-jdbc-0.245.1.jar");
        driverConfig.setJdbcDriverClass("com.facebook.presto.jdbc.PrestoDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/presto-jdbc-0.245.1.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
