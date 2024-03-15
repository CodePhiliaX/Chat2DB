package ai.chat2db.plugin.mongodb;

import ai.chat2db.spi.CommandExecutor;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Database;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.util.List;



public class MongodbMetaData extends DefaultMetaService implements MetaData {

    @Override
    public List<Database> databases(Connection connection) {
        return Lists.newArrayList();
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return new MongodbCommandExecutor();
    }
}
