package ai.chat2db.plugin.oceanbase.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("OceanBase");
        dbConfig.setDbType("OCEANBASE");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("oceanbase-client-2.4.2.jar");
        driverConfig.setJdbcDriverClass("com.oceanbase.jdbc.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/oceanbase-client-2.4.2.jar"));
        driverConfig.setName(driverConfig.getJdbcDriver() + ":" + driverConfig.getJdbcDriverClass());
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
