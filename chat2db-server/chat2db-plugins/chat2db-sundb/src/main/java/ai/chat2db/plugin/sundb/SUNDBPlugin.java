package ai.chat2db.plugin.sundb;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class SUNDBPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"sundb.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new SUNDBMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new SUNDBDBManage();
    }
}
