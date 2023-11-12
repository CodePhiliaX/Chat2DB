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
        driverConfig.setJdbcDriverClass("com.kingbase8.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/kingbase8-8.6.0.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);

        DriverConfig driverConfig1 = new DriverConfig();
        driverConfig1.setJdbcDriver("kingbase8-8.2.0.jar");
        driverConfig1.setJdbcDriverClass("com.kingbase8.Driver");
        driverConfig1.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/kingbase8-8.2.0.jar"));

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig,driverConfig1));
        return dbConfig;
    }
}
