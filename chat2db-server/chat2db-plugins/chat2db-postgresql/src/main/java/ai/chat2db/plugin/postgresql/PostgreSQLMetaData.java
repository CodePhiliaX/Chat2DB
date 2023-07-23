package ai.chat2db.plugin.postgresql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Schema;
import ai.chat2db.spi.sql.SQLExecutor;

import static ai.chat2db.plugin.postgresql.consts.SQLConst.FUNCTION_SQL;

public class PostgreSQLMetaData extends DefaultMetaService implements MetaData {

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        SQLExecutor.getInstance().executeSql(connection, FUNCTION_SQL.replaceFirst("tableSchema", schemaName),
            resultSet -> null);
        String ddlSql = "select showcreatetable('" + schemaName + "','" + tableName + "') as sql";
        return SQLExecutor.getInstance().executeSql(connection, ddlSql, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getString("sql");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    @Override
    public List<Database> databases(Connection connection) {
        return SQLExecutor.getInstance().executeSql(connection, "SELECT datname FROM pg_database;", resultSet -> {
            List<Database> databases = new ArrayList<>();
            try {
                while (resultSet.next()) {
                    String dbName = resultSet.getString("datname");
                    if ("template0".equals(dbName) || "template1".equals(dbName)) {
                        continue;
                    }
                    Database database = new Database();
                    database.setName(dbName);
                    databases.add(database);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return databases;
        });

    }

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        return SQLExecutor.getInstance().executeSql(connection,
            "SELECT catalog_name, schema_name FROM information_schema.schemata;", resultSet -> {
                List<Schema> databases = new ArrayList<>();
                try {
                    while (resultSet.next()) {
                        Schema schema = new Schema();
                        String name = resultSet.getString("schema_name");
                        String catalogName = resultSet.getString("catalog_name");
                        schema.setName(name);
                        schema.setDatabaseName(catalogName);
                        databases.add(schema);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return databases;
            });
    }
}
