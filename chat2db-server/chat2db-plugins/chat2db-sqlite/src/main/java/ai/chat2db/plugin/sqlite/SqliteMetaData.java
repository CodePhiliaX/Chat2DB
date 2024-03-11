package ai.chat2db.plugin.sqlite;

import ai.chat2db.plugin.sqlite.builder.SqliteBuilder;
import ai.chat2db.plugin.sqlite.type.SqliteCollationEnum;
import ai.chat2db.plugin.sqlite.type.SqliteColumnTypeEnum;
import ai.chat2db.plugin.sqlite.type.SqliteDefaultValueEnum;
import ai.chat2db.plugin.sqlite.type.SqliteIndexTypeEnum;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultMetaService;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.SQLExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SqliteMetaData extends DefaultMetaService implements MetaData {
    public static  String  VIEW_DDL_SQL="SELECT * FROM sqlite_master WHERE type = 'view' and name='%s';";
    @Override
    public Table view(Connection connection, String databaseName, String schemaName, String viewName) {
        Table view = new Table();
        String sql = String.format(VIEW_DDL_SQL,viewName);
        SQLExecutor.getInstance().execute(connection, sql, resultSet->{
            if (resultSet.next()) {
                view.setDatabaseName(databaseName);
                view.setDdl(resultSet.getString("sql"));
            }
        });
        return view;
    }

    public static final String TRIGGER_LIST_SQL = "SELECT * FROM sqlite_master WHERE type = 'trigger';";
    public static  String TRIGGER_DDL_SQL = "SELECT * FROM sqlite_master WHERE type = 'trigger' and name='%s';";

    @Override
    public List<Trigger> triggers(Connection connection, String databaseName, String schemaName) {
        List<Trigger> triggers = new ArrayList<>();
        return SQLExecutor.getInstance().execute(connection, TRIGGER_LIST_SQL, resultSet -> {
            while (resultSet.next()) {
                Trigger trigger = new Trigger();
                String triggerName = resultSet.getString("name");
                trigger.setTriggerName(triggerName);
                trigger.setDatabaseName(databaseName);
                triggers.add(trigger);
            }
            return triggers;
        });
    }

    @Override
    public Trigger trigger(Connection connection, String databaseName, String schemaName, String triggerName) {
        Trigger trigger = new Trigger();
        String sql = String.format(TRIGGER_DDL_SQL, triggerName);
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
            while (resultSet.next()) {
                trigger.setTriggerName(triggerName);
                trigger.setDatabaseName(databaseName);
                trigger.setTriggerBody(resultSet.getString("sql"));
            }
            return trigger;
        });
    }

    @Override
    public String tableDDL(Connection connection, String databaseName, String schemaName, String tableName) {
        String sql = "SELECT sql FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
        return SQLExecutor.getInstance().execute(connection, sql, resultSet -> {
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
        return Lists.newArrayList(Database.builder().name("main").build());
    }

    @Override
    public List<Schema> schemas(Connection connection, String databaseName) {
        return Lists.newArrayList();
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new SqliteBuilder();
    }

    @Override
    public TableMeta getTableMeta(String databaseName, String schemaName, String tableName) {
        return TableMeta.builder()
                .columnTypes(SqliteColumnTypeEnum.getTypes())
                .charsets(null)
                .collations(SqliteCollationEnum.getCollations())
                .indexTypes(SqliteIndexTypeEnum.getIndexTypes())
                .defaultValues(SqliteDefaultValueEnum.getDefaultValues())
                .build();
    }


    @Override
    public String getMetaDataName(String... names) {
        return Arrays.stream(names).filter(name -> StringUtils.isNotBlank(name)).map(name -> "\"" + name + "\"").collect(Collectors.joining("."));
    }
}
