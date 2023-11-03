package ai.chat2db.plugin.db2.builder;

import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import com.google.common.collect.Lists;

public class DBConfigBuilder {
    public static DBConfig buildDBConfig() {
        DBConfig dbConfig = new DBConfig();
        dbConfig.setName("DB2");
        dbConfig.setDbType("DB2");

        DriverConfig driverConfig = new DriverConfig();
        driverConfig.setJdbcDriver("db2jcc4_4.26.14.jar");
        driverConfig.setJdbcDriverClass("com.ibm.db2.jcc.DB2Driver");
        driverConfig.setDownloadJdbcDriverUrls(Lists.newArrayList("https://oss.sqlgpt.cn/lib/db2jcc4_4.26.14.jar"));
        dbConfig.setDefaultDriverConfig(driverConfig);

        dbConfig.setDriverConfigList(Lists.newArrayList(driverConfig));
        return dbConfig;
    }
}
