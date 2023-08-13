package ai.chat2db.plugin.sqlserver;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.Function;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.Trigger;
import ai.chat2db.spi.sql.SQLExecutor;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.StringUtils;

public class SqlServerMetaData extends DefaultMetaService implements MetaData {
    private String functionSQL
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

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        try {
            System.out.println(functionSQL);
            SQLExecutor.getInstance().executeSql(connection, functionSQL.replace("tableSchema", schemaName),
                resultSet -> null);
        } catch (Exception e) {
            //log.error("创建函数失败", e);
        }

        String ddlSql = "SELECT " + schemaName + ".ufn_GetCreateTableScript('" + schemaName + "', '" + tableName
            + "') AS sql";
        return SQLExecutor.getInstance().execute(connection, ddlSql, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getString("sql");
            }
            return null;
        });
    }

    private static String ROUTINES_SQL
        = "SELECT type_desc, OBJECT_NAME(object_id) AS FunctionName, OBJECT_DEFINITION(object_id) AS "
        + "definition FROM sys.objects WHERE type_desc IN(%s) and name = '%s' ;";

    @Override
    public Function function(Connection connection, @NotEmpty String databaseName, String schemaName,
        String functionName) {

        String sql = String.format(ROUTINES_SQL, "'SQL_SCALAR_FUNCTION', 'SQL_TABLE_VALUED_FUNCTION'", functionName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Function function = new Function();
            function.setDatabaseName(databaseName);
            function.setSchemaName(schemaName);
            function.setFunctionName(functionName);
            if (resultSet.next()) {
                function.setFunctionBody(resultSet.getString("definition"));
            }
            return function;
        });
    }

    @Override
    public List<Function> functions(Connection connection, String databaseName, String schemaName) {
        List<Function> functions = SQLExecutor.getInstance().functions(connection, databaseName, schemaName);
        return functions.stream().map(function -> removeVersion(function)).collect(Collectors.toList());
    }

    private Function removeVersion(Function function) {
        String fullFunctionName = function.getFunctionName();
        if (!StringUtils.isEmpty(fullFunctionName)) {
            String[] parts = fullFunctionName.split(";");
            String functionName = parts[0];
            function.setFunctionName(functionName);
        }
        return function;
    }

    @Override
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        List<Procedure> procedures = SQLExecutor.getInstance().procedures(connection, databaseName, schemaName);
        return procedures.stream().map(procedure -> removeVersion(procedure)).collect(Collectors.toList());
    }

    private Procedure removeVersion(Procedure procedure) {
        String fullProcedureName = procedure.getProcedureName();
        if (!StringUtils.isEmpty(fullProcedureName)) {
            String[] parts = fullProcedureName.split(";");
            String procedureName = parts[0];
            procedure.setProcedureName(procedureName);
        }
        return procedure;
    }

    private static String TRIGGER_SQL
        = "SELECT OBJECT_NAME(parent_obj) AS TableName, name AS triggerName, OBJECT_DEFINITION(id) AS "
        + "triggerDefinition, CASE WHEN status & 1 = 1 THEN 'Enabled' ELSE 'Disabled' END AS Status FROM sysobjects "
        + "WHERE xtype = 'TR' and name = '%s';";

    private static String TRIGGER_SQL_LIST
        = "SELECT OBJECT_NAME(parent_obj) AS TableName, name AS triggerName, OBJECT_DEFINITION(id) AS "
        + "triggerDefinition, CASE WHEN status & 1 = 1 THEN 'Enabled' ELSE 'Disabled' END AS Status FROM sysobjects "
        + "WHERE xtype = 'TR' ";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, TRIGGER_SQL_LIST, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                trigger.setTriggerName(resultSet.getString("triggerName"));
                trigger.setSchemaName(schemaName);
                trigger.setDatabaseName(databaseName);
                triggers.add(trigger);
            }
            return triggers;
        });
    }

    @Override
    public Trigger trigger(Connection connection, @NotEmpty String databaseName, String schemaName,
        String triggerName) {

        String sql = String.format(TRIGGER_SQL, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Trigger trigger = new Trigger();
            trigger.setDatabaseName(databaseName);
            trigger.setSchemaName(schemaName);
            trigger.setTriggerName(triggerName);
            if (resultSet.next()) {
                trigger.setTriggerBody(resultSet.getString("triggerDefinition"));
            }
            return trigger;
        });
    }

    @Override
    public Procedure procedure(Connection connection, @NotEmpty String databaseName, String schemaName,
        String procedureName) {
        String sql = String.format(ROUTINES_SQL, "'SQL_STORED_PROCEDURE'", procedureName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
                Procedure procedure = new Procedure();
                procedure.setDatabaseName(databaseName);
                procedure.setSchemaName(schemaName);
                procedure.setProcedureName(procedureName);
                if (resultSet.next()) {
                    procedure.setProcedureBody(resultSet.getString("definition"));
                }
                return procedure;
            }
        );
    }

    private static String VIEW_SQL
        = "SELECT TABLE_SCHEMA, TABLE_NAME, VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = '%s' "
        + "AND TABLE_NAME = '%s';";

    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        String sql = String.format(VIEW_SQL, schemaName, viewName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            Table table = new Table();
            table.setDatabaseName(databaseName);
            table.setSchemaName(schemaName);
            table.setName(viewName);
            if (resultSet.next()) {
                table.setDdl(resultSet.getString("VIEW_DEFINITION"));
            }
            return table;
        });
    }
}
