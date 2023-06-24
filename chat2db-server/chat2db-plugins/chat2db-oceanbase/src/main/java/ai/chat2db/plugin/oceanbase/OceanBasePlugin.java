package ai.chat2db.plugin.oceanbase;

import ai.chat2db.plugin.oceanbase.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.jdbc.DefaultMetaService;

public class OceanBasePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new OceanBaseMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new OceanBaseDBManage();
    }
}
