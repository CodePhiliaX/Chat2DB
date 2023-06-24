package ai.chat2db.plugin.sqlite;

import ai.chat2db.plugin.sqlite.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class SqlitePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new SqliteMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new SqliteDBManage();
    }
}
