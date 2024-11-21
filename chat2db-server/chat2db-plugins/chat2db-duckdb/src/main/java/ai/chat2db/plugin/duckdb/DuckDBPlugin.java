package ai.chat2db.plugin.duckdb;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class DuckDBPlugin implements Plugin {

    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"duckDB.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new DuckDBMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new DuckDBManage();
    }
}
