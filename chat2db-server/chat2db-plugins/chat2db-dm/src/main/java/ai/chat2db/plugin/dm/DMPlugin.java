package ai.chat2db.plugin.dm;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class DMPlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"dm.json", DBConfig.class);

    }

    @Override
    public MetaData getMetaData() {
        return new DMMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new DMDBManage();
    }
}
