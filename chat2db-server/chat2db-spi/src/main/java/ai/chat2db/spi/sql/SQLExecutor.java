package ai.chat2db.spi.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.util.ResultSetUtils;
import cn.hutool.core.date.TimeInterval;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

/**
 * Dbhub 统一数据库连接管理
 *
 * @author jipengfei
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public <R> R execute(Connection connection, String sql, ResultSetFunction<R> function) {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void executeSql(Connection connection, String sql, Consumer<List<Header>> headerConsumer,
        Consumer<List<String>> rowConsumer) {
        executeSql(connection, sql, headerConsumer, rowConsumer, true);
    }

    public void executeSql(Connection connection, String sql, Consumer<List<Header>> headerConsumer,
        Consumer<List<String>> rowConsumer, boolean limitSize) {
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
                            row.add(ai.chat2db.spi.util.JdbcUtils.getResultSetValue(rs, i, limitSize));
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
        return execute(sql, connection, true, null, null);
    }

    /**
     * 执行sql
     *
     * @param sql
     * @param connection
     * @param limitRowSize
     * @param offset
     * @param count
     * @return
     * @throws SQLException
     */
    public ExecuteResult execute(final String sql, Connection connection, boolean limitRowSize, Integer offset,
        Integer count)
        throws SQLException {
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

                    if (offset == null || offset < 0) {
                        offset = 0;
                    }
                    int rowNumber = 0;
                    int rowCount = 1;
                    while (rs.next()) {
                        if (rowNumber++ < offset) {
                            continue;
                        }
                        List<String> row = Lists.newArrayListWithExpectedSize(col);
                        dataList.add(row);
                        for (int i = 1; i <= col; i++) {
                            row.add(ai.chat2db.spi.util.JdbcUtils.getResultSetValue(rs, i, limitRowSize));
                        }
                        if (count != null && count > 0 && rowCount++ >= count) {
                            break;
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
        return execute(sql, connection, true, null, null);
    }

    /**
     * 获取所有的数据库
     *
     * @param connection
     * @return
     */
    public List<Database> databases(Connection connection) {
        try (ResultSet resultSet = connection.getMetaData().getCatalogs();) {
            return ResultSetUtils.toObjectList(resultSet, Database.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the schema names available in this database. The results are ordered by TABLE_CATALOG and TABLE_SCHEM.
     * The schema columns are:
     * TABLE_SCHEM String => schema name
     * TABLE_CATALOG String => catalog name (may be null)
     * Params:
     * catalog – a catalog name; must match the catalog name as it is stored in the database;"" retrieves those without
     * a catalog; null means catalog name should not be used to narrow down the search. schemaPattern – a schema name;
     * must match the schema name as it is stored in the database; null means schema name should not be used to narrow
     * down the search.
     * Returns:
     * a ResultSet object in which each row is a schema description
     * Throws:
     * SQLException – if a database access error occurs
     * Since:
     * 1.6
     * See Also:
     * getSearchStringEscape
     */
    public List<Schema> schemas(Connection connection, String databaseName, String schemaName) {
        if (StringUtils.isEmpty(databaseName) && StringUtils.isEmpty(schemaName)) {
            try (ResultSet resultSet = connection.getMetaData().getSchemas()) {
                return ResultSetUtils.toObjectList(resultSet, Schema.class);
            } catch (SQLException e) {
                throw new RuntimeException("Get schemas error", e);
            }
        }
        try (ResultSet resultSet = connection.getMetaData().getSchemas(databaseName, schemaName)) {
            return ResultSetUtils.toObjectList(resultSet, Schema.class);
        } catch (SQLException e) {
            throw new RuntimeException("Get schemas error", e);
        }
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
        try (ResultSet resultSet = connection.getMetaData().getTables(databaseName, schemaName, tableName,
            types)) {
            return ResultSetUtils.toObjectList(resultSet, Table.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        try (ResultSet resultSet = connection.getMetaData().getColumns(databaseName, schemaName, tableName,
            columnName)) {
            return ResultSetUtils.toObjectList(resultSet, TableColumn.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get all table index info
     *
     * @param connection   connection
     * @param databaseName databaseName of the index
     * @param schemaName   schemaName of the index
     * @param tableName    tableName of the index
     * @return List<TableIndex> table index list
     */
    public List<TableIndex> indexes(Connection connection, String databaseName, String schemaName, String tableName) {
        List<TableIndex> tableIndices = Lists.newArrayList();
        try (ResultSet resultSet = connection.getMetaData().getIndexInfo(databaseName, schemaName, tableName,
            false,
            false)) {
            List<TableIndexColumn> tableIndexColumns = ResultSetUtils.toObjectList(resultSet, TableIndexColumn.class);
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
     * Get all functions available in a catalog.
     *
     * @param connection   connection
     * @param databaseName databaseName of the function
     * @param schemaName   schemaName of the function
     * @return List<Function>
     */
    public List<ai.chat2db.spi.model.Function> functions(Connection connection, String databaseName,
        String schemaName) {
        try (ResultSet resultSet = connection.getMetaData().getFunctions(databaseName, schemaName, null);) {
            return ResultSetUtils.toObjectList(resultSet, ai.chat2db.spi.model.Function.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * procedure list
     *
     * @param connection   connection
     * @param databaseName databaseName
     * @param schemaName   schemaName
     * @return List<Procedure>
     */
    public List<Procedure> procedures(Connection connection, String databaseName, String schemaName) {
        try (ResultSet resultSet = connection.getMetaData().getProcedures(databaseName, schemaName, null)) {
            return ResultSetUtils.toObjectList(resultSet, Procedure.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
