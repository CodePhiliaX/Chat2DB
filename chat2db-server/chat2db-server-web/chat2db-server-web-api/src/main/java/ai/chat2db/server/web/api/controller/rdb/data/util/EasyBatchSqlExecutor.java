package ai.chat2db.server.web.api.controller.rdb.data.util;

import ai.chat2db.server.web.api.controller.rdb.data.DataFileFactoryProducer;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author: zgq
 * @date: 2024年05月08日 12:30
 */
public class EasyBatchSqlExecutor {
    public static void executeBatchInsert(Connection connection, List<String> sqlCacheList) {
        try (Statement stmt = connection.createStatement()) {
            for (String sql : sqlCacheList) {
                stmt.addBatch(sql);
            }
            connection.setAutoCommit(false);
            try {
                stmt.executeBatch();
                connection.commit();
            } catch (BatchUpdateException e) {
                connection.rollback();
                handleErrorSql(stmt,sqlCacheList);
            }
            stmt.clearBatch();
            sqlCacheList.clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleErrorSql(Statement stmt, List<String> sqlCacheList) {
        for (String sql : sqlCacheList) {
            try {
                stmt.execute(sql);
            } catch (SQLException ex) {
                DataFileFactoryProducer.notifyError(sql + "error msg:" + ex.getMessage() + "");
            }
        }
    }

}
