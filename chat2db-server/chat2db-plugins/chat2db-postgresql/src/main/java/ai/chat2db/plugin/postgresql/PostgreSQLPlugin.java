package ai.chat2db.plugin.postgresql;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class PostgreSQLPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"pg.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new PostgreSQLMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new PostgreSQLDBManage();
    }
}
