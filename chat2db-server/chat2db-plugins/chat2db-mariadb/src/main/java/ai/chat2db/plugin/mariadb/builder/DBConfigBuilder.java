package ai.chat2db.plugin.mariadb.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("MariaDB");
        dbConfig.setDbType("MARIADB");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("mariadb-java-client-3.0.8.jar");
        driverConfig.setJdbcDriverClass("org.mariadb.jdbc.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/mariadb-java-client-3.0.8.jar"));
        driverConfig.setName(driverConfig.getJdbcDriver() + ":" + driverConfig.getJdbcDriverClass());
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
