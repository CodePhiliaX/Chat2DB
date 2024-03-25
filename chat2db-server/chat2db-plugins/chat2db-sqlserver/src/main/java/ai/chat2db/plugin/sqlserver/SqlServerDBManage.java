package ai.chat2db.plugin.sqlserver;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.sql.SQLExecutor;

import java.sql.*;
import java.util.Objects;

public class SqlServerDBManage extends DefaultDBManage implements DBManage {
    private String tableDDLFunction
            = "CREATE FUNCTION tableSchema.ufn_GetCreateTableScript( @schema_name NVARCHAR(128), @table_name NVARCHAR"
            + "(128)) RETURNS NVARCHAR(MAX) AS BEGIN DECLARE @CreateTableScript NVARCHAR(MAX); DECLARE @IndexScripts "
            + "NVARCHAR(MAX) = ''; DECLARE @ColumnDescriptions NVARCHAR(MAX) = N''; SELECT @CreateTableScript = CONCAT( "
            + "'CREATE TABLE [', s.name, '].[' , t.name, '] (', STUFF( ( SELECT ', [' + c.name + '] ' + tp.name + CASE "
            + "WHEN tp.name IN ('varchar', 'nvarchar', 'char', 'nchar') THEN '(' + IIF(c.max_length = -1, 'MAX', CAST(c"
            + ".max_length AS NVARCHAR(10))) + ')' WHEN tp.name IN ('decimal', 'numeric') THEN '(' + CAST(c.precision AS "
            + "NVARCHAR(10)) + ', ' + CAST(c.scale AS NVARCHAR(10)) + ')' ELSE '' END + ' ' + CASE WHEN c.is_nullable = 1"
            + " THEN 'NULL' ELSE 'NOT NULL' END FROM sys.columns c JOIN sys.types tp ON c.user_type_id = tp.user_type_id "
            + "WHERE c.object_id = t.object_id FOR XML PATH(''), TYPE ).value('/', 'nvarchar(max)'), 1, 1, ''), ');' ) "
            + "FROM sys.tables t JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE t.name = @table_name AND s.name = "
            + "@schema_name; SELECT @IndexScripts = @IndexScripts + 'CREATE ' + CASE WHEN i.is_unique = 1 THEN 'UNIQUE ' "
            + "ELSE '' END + i.type_desc + ' INDEX [' + i.name + '] ON [' + s.name + '].[' + t.name + '] (' + STUFF( ( "
            + "SELECT ', [' + c.name + ']' + CASE WHEN ic.is_descending_key = 1 THEN ' DESC' ELSE ' ASC' END FROM sys"
            + ".index_columns ic JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id WHERE ic"
            + ".object_id = i.object_id AND ic.index_id = i.index_id ORDER BY ic.key_ordinal FOR XML PATH('') ), 1, 1, "
            + "'') + ')' + CASE WHEN i.has_filter = 1 THEN ' WHERE ' + i.filter_definition ELSE '' END + ';' + CHAR(13) +"
            + " CHAR(10) FROM sys.indexes i JOIN sys.tables t ON i.object_id = t.object_id JOIN sys.schemas s ON t"
            + ".schema_id = s.schema_id WHERE i.type > 0 AND t.name = @table_name AND s.name "
            + "= @schema_name; SELECT @ColumnDescriptions += 'EXEC sp_addextendedproperty @name=N''MS_Description'', "
            + "@value=N''' + CAST(p.value AS NVARCHAR(MAX)) + ''', @level0type=N''SCHEMA'', @level0name=N''' + "
            + "@schema_name + ''', @level1type=N''TABLE'', @level1name=N''' + @table_name + ''', @level2type=N''COLUMN'',"
            + " @level2name=N''' + c.name + ''';' + CHAR(13) + CHAR(10) FROM sys.extended_properties p JOIN sys.columns c"
            + " ON p.major_id = c.object_id AND p.minor_id = c.column_id JOIN sys.tables t ON c.object_id = t.object_id "
            + "JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE p.class = 1 AND t.name = @table_name AND s.name = "
            + "@schema_name; SET @CreateTableScript = @CreateTableScript + CHAR(13) + CHAR(10) + @IndexScripts + CHAR(13)"
            + " + CHAR(10)+ @ColumnDescriptions+ CHAR(10); RETURN @CreateTableScript; END";

    private static String TRIGGER_SQL_LIST
            = "SELECT OBJECT_NAME(parent_obj) AS TableName, name AS triggerName, OBJECT_DEFINITION(id) AS "
            + "triggerDefinition, CASE WHEN status & 1 = 1 THEN 'Enabled' ELSE 'Disabled' END AS Status FROM sysobjects "
            + "WHERE xtype = 'TR' ";

    @Override
    public String exportDatabase(Connection connection, String databaseName, String schemaName, boolean containData) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder();
        exportTables(connection, sqlBuilder, schemaName, containData);
        exportViews(connection, databaseName, schemaName, sqlBuilder);
        exportFunctions(connection, schemaName, sqlBuilder);
        exportProcedures(connection, schemaName, sqlBuilder);
        exportTriggers(connection, sqlBuilder);
        return sqlBuilder.toString();
    }

    private void exportTables(Connection connection, StringBuilder sqlBuilder, String schemaName, boolean containData) throws SQLException {
        String sql ="SELECT name FROM SysObjects Where XType='U'";
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String tableName = resultSet.getString("name");
                exportTable(connection, tableName, schemaName, sqlBuilder, containData);
            }
        }
    }


    private void exportTable(Connection connection, String tableName, String schemaName, StringBuilder sqlBuilder, boolean containData) throws SQLException {
        try {
            SQLExecutor.getInstance().execute(connection, tableDDLFunction.replace("tableSchema", schemaName),
                                              resultSet -> null);
        } catch (Exception e) {
            //log.error("Failed to create function", e);
        }
        String sql = String.format("SELECT %s.ufn_GetCreateTableScript('%s', '%s') as ddl",schemaName,schemaName,tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append("DROP TABLE IF EXISTS ").append(tableName).append(";").append("\n")
                        .append(resultSet.getString("ddl")).append("\n");
                if (containData) {
                    exportTableData(connection, tableName, sqlBuilder);
                } else {
                    sqlBuilder.append("go").append("\n");
                }
            }
        }
    }


    private void exportTableData(Connection connection, String tableName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("select * from %s", tableName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                sqlBuilder.append("INSERT INTO ").append(tableName).append(" VALUES (");
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = resultSet.getString(i);
                    if (Objects.isNull(value)) {
                        sqlBuilder.append("NULL");
                    } else {
                        sqlBuilder.append("'").append(value).append("'");
                    }
                    if (i < metaData.getColumnCount()) {
                        sqlBuilder.append(", ");
                    }
                }
                sqlBuilder.append(");\n");
            }
            sqlBuilder.append("\n");
        }
        sqlBuilder.append("go").append("\n");
    }

    private void exportViews(Connection connection, String databaseName, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT TABLE_NAME, VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS " +
                                           "WHERE TABLE_SCHEMA = '%s' AND TABLE_CATALOG = '%s'; ", schemaName, databaseName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                sqlBuilder.append("DROP VIEW IF EXISTS ").append(resultSet.getString("TABLE_NAME")).append(";\n").append("go").append("\n")
                        .append(resultSet.getString("VIEW_DEFINITION")).append(";").append("\n")
                        .append("go").append("\n");
            }

        }
    }

    private void exportFunctions(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT name FROM sys.objects WHERE type = 'FN' and SCHEMA_ID = SCHEMA_ID('%s')", schemaName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String functionName = resultSet.getString("name");
                exportFunction(connection, functionName, schemaName, sqlBuilder);
            }
        }
    }

    private void exportFunction(Connection connection, String functionName, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT OBJECT_DEFINITION(OBJECT_ID('%s.%s')) as ddl", schemaName, functionName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("ddl")
                                          .replace("CREATE   FUNCTION", "CREATE OR ALTER FUNCTION"))
                        .append("\n").append("go").append("\n");

            }
        }
    }

    private void exportProcedures(Connection connection, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT name FROM sys.procedures WHERE SCHEMA_ID = SCHEMA_ID('%s')", schemaName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            while (resultSet.next()) {
                String procedureName = resultSet.getString("name");
                exportProcedure(connection, procedureName, schemaName, sqlBuilder);
            }
        }
    }

    private void exportProcedure(Connection connection, String procedureName, String schemaName, StringBuilder sqlBuilder) throws SQLException {
        String sql = String.format("SELECT definition FROM sys.sql_modules  WHERE object_id = (OBJECT_ID('%s.%s'));", schemaName, procedureName);
        try (ResultSet resultSet = connection.createStatement().executeQuery(sql)) {
            if (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("definition")
                                          .replace("CREATE   PROCEDURE", "CREATE OR ALTER PROCEDURE"))
                        .append("\n").append("go").append("\n");

            }
        }
    }

    private void exportTriggers(Connection connection, StringBuilder sqlBuilder) throws SQLException {
        try (ResultSet resultSet = connection.createStatement().executeQuery(TRIGGER_SQL_LIST)) {
            while (resultSet.next()) {
                sqlBuilder.append(resultSet.getString("triggerDefinition")
                                          .replace("CREATE   TRIGGER", "CREATE OR ALTER TRIGGER"))
                        .append("\n").append("go").append("\n");
            }
        }
    }
    @Override
    public void connectDatabase(Connection connection, String database) {
        try {
            SQLExecutor.getInstance().execute(connection, "use [" + database + "];");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
