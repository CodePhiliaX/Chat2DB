package ai.chat2db.plugin.oracle;


import ai.chat2db.plugin.oracle.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class OraclePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new OracleMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new OracleDBManage();
    }
}
