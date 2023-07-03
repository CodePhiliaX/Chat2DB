package ai.chat2db.plugin.redis.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("Redis");
        dbConfig.setDbType("REDIS");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("redis-jdbc-driver-1.3.jar");
        driverConfig.setJdbcDriverClass("jdbc.RedisDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss-chat2db.alibaba.com/lib/redis-jdbc-driver-1.3.jar"));
        driverConfig.setName(driverConfig.getJdbcDriver() + ":" + driverConfig.getJdbcDriverClass());
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
