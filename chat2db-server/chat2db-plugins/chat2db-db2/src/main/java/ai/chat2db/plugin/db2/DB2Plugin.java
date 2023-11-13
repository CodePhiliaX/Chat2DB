package ai.chat2db.plugin.db2;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class DB2Plugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"db2.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new DB2MetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new DB2DBManage();
    }
}
