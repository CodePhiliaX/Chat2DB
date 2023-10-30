package ai.chat2db.plugin.oracle.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("Oracle");
        dbConfig.setDbType("ORACLE");

        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("ojdbc8-19.3.0.0.jar,orai18n-19.3.0.0.jar");
        driverConfig.setJdbcDriverClass("oracle.jdbc.driver.OracleDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/ojdbc8-19.3.0.0.jar", "https://oss.sqlgpt.cn/lib/orai18n-19.3.0.0.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
