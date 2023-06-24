package ai.chat2db.plugin.kingbase.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("KingBase");
        dbConfig.setDbType("KINGBASE");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("kingbase8-8.6.0.jar");
        driverConfig.setJdbcDriverClass("om.kingbase8.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/kingbase8-8.6.0.jar"));
        driverConfig.setName(driverConfig.getJdbcDriver() + ":" + driverConfig.getJdbcDriverClass());
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
