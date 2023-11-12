package ai.chat2db.plugin.oracle;


import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.util.FileUtils;

public class OraclePlugin implements Plugin {
    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"oracle.json", DBConfig.class);

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
