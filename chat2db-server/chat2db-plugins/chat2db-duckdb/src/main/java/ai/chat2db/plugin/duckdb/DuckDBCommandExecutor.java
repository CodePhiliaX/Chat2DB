package ai.chat2db.plugin.duckdb;

import ai.chat2db.spi.model.Command;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.sql.SQLExecutor;

import java.util.List;

public class DuckDBCommandExecutor extends SQLExecutor {

    @Override
    public List<ExecuteResult> executeSelectTable(Command command) {
        String sql = "select * from " +command.getSchemaName() + "." + command.getTableName();
        command.setScript(sql);
        return execute(command);
    }
}
