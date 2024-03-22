package ai.chat2db.plugin.kingbase;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class KingBasePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"kingbase.json", DBConfig.class);
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
