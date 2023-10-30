package ai.chat2db.plugin.mongodb.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("Mongodb");
        dbConfig.setDbType("MONGODB");
        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("mongo-jdbc-standalone-1.18.jar");
        driverConfig.setJdbcDriverClass("com.dbschema.MongoJdbcDriver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/mongo-jdbc-standalone-1.18.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);
        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
