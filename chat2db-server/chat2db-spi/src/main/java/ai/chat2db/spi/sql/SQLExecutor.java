package ai.chat2db.spi.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.model.ExecuteResult;
import ai.chat2db.spi.model.Header;
import ai.chat2db.spi.model.Procedure;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.model.TableIndexColumn;
import ai.chat2db.spi.util.ResultSetUtils;
import cn.hutool.core.date.TimeInterval;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import static ai.chat2db.spi.util.ResultSetUtils.buildColumn;
import static ai.chat2db.spi.util.ResultSetUtils.buildFunction;
import static ai.chat2db.spi.util.ResultSetUtils.buildProcedure;
import static ai.chat2db.spi.util.ResultSetUtils.buildTable;
import static ai.chat2db.spi.util.ResultSetUtils.buildTableIndexColumn;

/**
 * Dbhub 统一数据库连接管理
 * TODO 长时间不用连接可以关闭，待优化
 *
 * @author jipengfei
 * @version : DbhubDataSource.java
 */
@Slf4j
public class SQLExecutor {
    /**
     * 全局单例
     */
    private static final SQLExecutor INSTANCE = new SQLExecutor();

    private SQLExecutor() {
    }

    public static SQLExecutor getInstance() {
        return INSTANCE;
    }

    //public Connection connection throws SQLException {
    //    return Chat2DBContext.connection;
    //}

    public void close() {
    }

    /**
     * 执行sql
     *
     * @param connection
     * @param sql
     * @param function
     * @return
     */

    public <R> R executeSql(Connection connection, String sql, Function<ResultSet, R> function) {
        if (StringUtils.isBlank(sql)) {
            return null;
        }
        log.info("execute:{}", sql);
        try (Statement stmt = connection.createStatement();) {
            boolean query = stmt.execute(sql);
            // 代表是查询
            if (query) {
                try (ResultSet rs = stmt.getResultSet();) {
                    return function.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void executeSql(Connection connection, String sql, Consumer<List<Header>> headerConsumer,
        Consumer<List<String>> rowConsumer) {
        Assert.notNull(sql, "SQL must not be null");
        log.info("execute:{}", sql);
        try (Statement stmt = connection.createStatement();) {
            boolean query = stmt.execute(sql);
            // 代表是查询
            if (query) {
                ResultSet rs = null;
                try {
                    rs = stmt.getResultSet();
                    // 获取有几列
                    ResultSetMetaData resultSetMetaData = rs.getMetaData();
                    int col = resultSetMetaData.getColumnCount();

                    // 获取header信息
                    List<Header> headerList = Lists.newArrayListWithExpectedSize(col);
                    for (int i = 1; i <= col; i++) {
                        headerList.add(Header.builder()
                            .dataType(ai.chat2db.spi.util.JdbcUtils.resolveDataType(
                                resultSetMetaData.getColumnTypeName(i), resultSetMetaData.getColumnType(i)).getCode())
                            .name(ResultSetUtils.getColumnName(resultSetMetaData, i))
                            .build());
                    }
                    headerConsumer.accept(headerList);

                    while (rs.next()) {
                        List<String> row = Lists.newArrayListWithExpectedSize(col);
                        for (int i = 1; i <= col; i++) {
                            row.add(ai.chat2db.spi.util.JdbcUtils.getResultSetValue(rs, i));
                        }
                        rowConsumer.accept(row);
                    }
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行sql
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public ExecuteResult execute(final String sql, Connection connection) throws SQLException {
        Assert.notNull(sql, "SQL must not be null");
        log.info("execute:{}", sql);

        ExecuteResult executeResult = ExecuteResult.builder().sql(sql).success(Boolean.TRUE).build();
        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(EasyToolsConstant.MAX_PAGE_SIZE);
            TimeInterval timeInterval = new TimeInterval();
            boolean query = stmt.execute(sql);
            executeResult.setDescription(I18nUtils.getMessage("sqlResult.success"));
            // 代表是查询
            if (query) {
                ResultSet rs = null;
                try {
                    rs = stmt.getResultSet();
                    // 获取有几列
                    ResultSetMetaData resultSetMetaData = rs.getMetaData();
                    int col = resultSetMetaData.getColumnCount();

                    // 获取header信息
                    List<Header> headerList = Lists.newArrayListWithExpectedSize(col);
                    executeResult.setHeaderList(headerList);
                    for (int i = 1; i <= col; i++) {
                        headerList.add(Header.builder()
                            .dataType(ai.chat2db.spi.util.JdbcUtils.resolveDataType(
                                resultSetMetaData.getColumnTypeName(i), resultSetMetaData.getColumnType(i)).getCode())
                            .name(ResultSetUtils.getColumnName(resultSetMetaData, i))
                            .build());
                    }

                    // 获取数据信息
                    List<List<String>> dataList = Lists.newArrayList();
                    executeResult.setDataList(dataList);

                    int n = 0;
                    while (rs.next() && n < EasyToolsConstant.MAX_PAGE_SIZE) {
                        n++;
                        List<String> row = Lists.newArrayListWithExpectedSize(col);
                        dataList.add(row);
                        for (int i = 1; i <= col; i++) {
                            row.add(ai.chat2db.spi.util.JdbcUtils.getResultSetValue(rs, i));
                        }
                    }
                    executeResult.setDuration(timeInterval.interval());
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            } else {
                executeResult.setDuration(timeInterval.interval());
                // 修改或者其他
                executeResult.setUpdateCount(stmt.getUpdateCount());
            }
        }
        return executeResult;
    }

    /**
     * 执行sql
     *
     * @param connection
     * @param sql
     * @return
     * @throws SQLException
     */
    public ExecuteResult execute(Connection connection, String sql) throws SQLException {
        return execute(sql, connection);
    }

    /**
     * 获取所有的数据库
     *
     * @param connection
     * @return
     */
    public List<String> databases(Connection connection) {
        List<String> tables = Lists.newArrayList();
        try (ResultSet resultSet = connection.getMetaData().getCatalogs();) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    tables.add(resultSet.getString("TABLE_CAT"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tables;
    }

    /**
     * 获取所有的schema
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @return
     */
    public List<Map<String, String>> schemas(Connection connection, String databaseName, String schemaName) {
        List<Map<String, String>> schemaList = Lists.newArrayList();
        if (StringUtils.isEmpty(databaseName) && StringUtils.isEmpty(schemaName)) {
            try (ResultSet resultSet = connection.getMetaData().getSchemas()) {
                if (resultSet != null) {
                    while (resultSet.next()) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name", resultSet.getString("TABLE_SCHEM"));
                        map.put("databaseName", resultSet.getString("TABLE_CATALOG"));
                        schemaList.add(map);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Get schemas error", e);
            }
            return schemaList;
        }
        try (ResultSet resultSet = connection.getMetaData().getSchemas(databaseName, schemaName)) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", resultSet.getString("TABLE_SCHEM"));
                    map.put("databaseName", resultSet.getString("TABLE_CATALOG"));
                    schemaList.add(map);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get schemas error", e);
        }
        return schemaList;
    }

    /**
     * 获取所有的数据库表
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @param types
     * @return
     */
    public List<Table> tables(Connection connection, String databaseName, String schemaName, String tableName,
        String types[]) {
        List<Table> tables = Lists.newArrayList();
        int n = 0;
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, schemaName, tableName,
            types)) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    n++;
                    tables.add(buildTable(resultSet));
                    if (n >= 5000) {// 最多只取5000条
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tables;
    }

    /**
     * 获取所有的数据库表列
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @param columnName
     * @return
     */
    public List<TableColumn> columns(Connection connection, String databaseName, String schemaName, String tableName,
        String columnName) {
        List<TableColumn> tableColumns = Lists.newArrayList();
        try (ResultSet resultSet = connection.getMetaData().getColumns(databaseName, schemaName, tableName,
            columnName)) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    tableColumns.add(buildColumn(resultSet));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tableColumns;
    }

    /**
     * 获取所有的数据库表索引
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @return
     */
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        List<TableIndex> tableIndices = Lists.newArrayList();
        try (ResultSet resultSet = connection.getMetaData().getIndexInfo(databaseName, schemaName, tableName,
            false,
            false)) {
            List<TableIndexColumn> tableIndexColumns = Lists.newArrayList();

            while (resultSet != null && resultSet.next()) {
                tableIndexColumns.add(buildTableIndexColumn(resultSet));
            }

            tableIndexColumns.stream().filter(c -> c.getIndexName() != null).collect(
                    Collectors.groupingBy(TableIndexColumn::getIndexName)).entrySet()
                .stream().forEach(entry -> {
                    TableIndex tableIndex = new TableIndex();
                    TableIndexColumn column = entry.getValue().get(0);
                    tableIndex.setName(entry.getKey());
                    tableIndex.setTableName(column.getTableName());
                    tableIndex.setSchemaName(column.getSchemaName());
                    tableIndex.setDatabaseName(column.getDatabaseName());
                    tableIndex.setUnique(!column.getNonUnique());
                    tableIndex.setColumnList(entry.getValue());
                    tableIndices.add(tableIndex);
                });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableIndices;
    }

    /**
     * 获取所有的函数
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @return
     */
    public List<ai.chat2db.spi.model.Function> functions(Connection connection, String databaseName,
        String schemaName) {
        List<ai.chat2db.spi.model.Function> functions = Lists.newArrayList();
        try (ResultSet resultSet = connection.getMetaData().getFunctions(databaseName, schemaName, null);) {
            while (resultSet != null && resultSet.next()) {
                functions.add(buildFunction(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return functions;
    }

    /**
     * 获取所有的存储过程
     *
     * @param connection
     * @param databaseName
     * @param schemaName
     * @return
     */
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        List<Procedure> procedures = Lists.newArrayList();
        try (ResultSet resultSet = connection.getMetaData().getProcedures(databaseName, schemaName, null)) {
            while (resultSet != null && resultSet.next()) {
                procedures.add(buildProcedure(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return procedures;
    }

}
