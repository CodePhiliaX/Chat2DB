package ai.chat2db.plugin.mongodb;

import ai.chat2db.plugin.mongodb.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class MongodbPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new MongodbMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MongodbManage();
    }
}
