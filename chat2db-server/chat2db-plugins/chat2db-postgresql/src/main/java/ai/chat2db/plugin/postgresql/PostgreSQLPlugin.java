package ai.chat2db.plugin.postgresql;

import ai.chat2db.plugin.postgresql.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class PostgreSQLPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new PostgreSQLMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new PostgreSQLDBManage();
    }
}
