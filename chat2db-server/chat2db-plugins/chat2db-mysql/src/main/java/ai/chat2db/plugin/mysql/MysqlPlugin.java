package ai.chat2db.plugin.mysql;

import ai.chat2db.plugin.mysql.builder.DBConfigBuilder;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;

public class MysqlPlugin implements Plugin {

    @Override
    public DBConfig getDBConfig() {
        return DBConfigBuilder.buildDBConfig();
    }

    @Override
    public MetaData getMetaData() {
        return new MysqlMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MysqlDBManage();
    }
}
