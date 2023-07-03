package ai.chat2db.plugin.mysql.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {

    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("Mysql");
        dbConfig.setDbType("MYSQL");

        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("mysql-connector-java-8.0.30.jar");
        driverConfig.setJdbcDriverClass("com.mysql.cj.jdbc.Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/mysql-connector-java-8.0.30.jar"));
        driverConfig.setName(driverConfig.getJdbcDriver() + ":" + driverConfig.getJdbcDriverClass());
        dbConfig.setDefaultDriverConfig(driverConfig);


        DriverConfig driverConfig2 = new DriverConfig();
        driverConfig2.setJdbcDriver("mysql-connector-java-5.1.47.jar");
        driverConfig2.setJdbcDriverClass("com.mysql.jdbc.Driver");
        driverConfig2.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/mysql-connector-java-5.1.47.jar"));
        driverConfig2.setName(driverConfig2.getJdbcDriver() + ":" + driverConfig2.getJdbcDriverClass());

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig,driverConfig2));
        return dbConfig;
    }
}
