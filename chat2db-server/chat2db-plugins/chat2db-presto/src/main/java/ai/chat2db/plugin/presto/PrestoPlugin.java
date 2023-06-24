package ai.chat2db.plugin.presto;


import ai.chat2db.plugin.presto.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class PrestoPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new PrestoMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new PrestoDBManage();
    }
}
