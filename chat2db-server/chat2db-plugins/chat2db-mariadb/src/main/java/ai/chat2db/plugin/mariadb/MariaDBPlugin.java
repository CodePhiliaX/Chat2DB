package ai.chat2db.plugin.mariadb;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class MariaDBPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"mariadb.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new MariaDBMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MariaDBManage();
    }
}
