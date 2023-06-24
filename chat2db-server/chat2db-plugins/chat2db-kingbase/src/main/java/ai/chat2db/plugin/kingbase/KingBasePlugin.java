package ai.chat2db.plugin.kingbase;

import ai.chat2db.plugin.kingbase.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class KingBasePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new KingBaseMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new KingBaseDBManage();
    }
}
