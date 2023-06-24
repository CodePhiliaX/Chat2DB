package ai.chat2db.plugin.hive;

import ai.chat2db.plugin.hive.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class HivePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new HiveMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new HiveDBManage();
    }
}
