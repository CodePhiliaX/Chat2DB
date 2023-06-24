package ai.chat2db.plugin.sqlserver;


import ai.chat2db.plugin.sqlserver.builder.SqlServerDBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class SqlServerPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return SqlServerDBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new SqlServerMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new SqlServerDBManage();
    }
}
