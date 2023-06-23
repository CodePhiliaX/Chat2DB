package ai.chat2db.plugin.h2;

import ai.chat2db.plugin.h2.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.jdbc.DefaultMetaService;

public class H2Plugin extends DefaultMetaService implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new H2Meta();
    }

    @Override
    public DBManage getDBManage() {
        return new H2DBManage();
    }
}
