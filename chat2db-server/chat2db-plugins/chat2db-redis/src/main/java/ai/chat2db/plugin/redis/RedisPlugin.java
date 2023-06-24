package ai.chat2db.plugin.redis;


import ai.chat2db.plugin.redis.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class RedisPlugin implements Plugin {

    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new RedisMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new RedisDBManage();
    }
}
