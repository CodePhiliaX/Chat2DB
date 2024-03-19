package ai.chat2db.plugin.h2;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class H2DBManage extends DefaultDBManage implements DBManage {
    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportSchema(connection, schemaName, sqlBuilder, containData);
        return sqlBuilder.toString();
    }

    private void exportSchema(Connection connection, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        String sql = String.format("SCRIPT NODATA NOPASSWORDS NOSETTINGS DROP SCHEMA %s;", schemaName);
        if (containData) {
            sql = sql.replace("NODATA", "");
        }
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String script = resultSet.getString("SCRIPT");
                if (!(script.startsWith("CREATE USER")||script.startsWith("--"))) {
                    sqlBuilder.append(script);
                    sqlBuilder.append("\n");
                }
            }
        }

    }

    @Override
    public void connectDatabase(Connection connection, String database) {
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (ObjectUtils.anyNull(connectInfo) || StringUtils.isEmpty(connectInfo.getSchemaName())) {
            return;
        }
        String schemaName = connectInfo.getSchemaName();
        try {
            SQLExecutor.getInstance().execute(connection, "SET SCHEMA \"" + schemaName + "\"");
        } catch (SQLException e) {

        }
    }


    @Override
    public void dropTable(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "DROP TABLE " + tableName;
        SQLExecutor.getInstance().execute(connection, sql, resultSet -> null);
    }
}
