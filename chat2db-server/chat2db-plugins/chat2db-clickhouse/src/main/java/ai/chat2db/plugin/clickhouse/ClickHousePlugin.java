package ai.chat2db.plugin.clickhouse;


import ai.chat2db.plugin.clickhouse.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class ClickHousePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new ClickHouseMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new ClickHouseDBManage();
    }
}
