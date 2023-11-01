package ai.chat2db.plugin.clickhouse.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("ClickHouse");
        dbConfig.setDbType("CLICKHOUSE");

        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("clickhouse-jdbc-0.3.2-patch8-http.jar");
        driverConfig.setJdbcDriverClass("com.clickhouse.jdbc.ClickHouseDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/clickhouse-jdbc-0.3.2-patch8-http.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}

