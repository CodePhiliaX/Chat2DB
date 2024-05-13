package ai.chat2db.plugin.mongodb;

import ai.chat2db.spi.model.Command;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.sql.SQLExecutor;

import java.util.List;

public class MongodbCommandExecutor extends SQLExecutor {

    @Override
    public List<ExecuteResult> executeSelectTable(Command command) {
        String sql = "db." + command.getTableName() + ".find()";
        command.setScript(sql);
        return execute(command);
    }
}
