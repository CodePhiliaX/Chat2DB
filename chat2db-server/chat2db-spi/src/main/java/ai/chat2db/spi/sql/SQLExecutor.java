/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.spi.sql;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.spi.model.*;
import cn.hutool.core.date.TimeInterval;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ai.chat2db.spi.util.ResultSetUtils.*;

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

    public Connection getConnection() throws SQLException {
        return Chat2DBContext.getConnection();
    }

    public void close() {
    }

    /**
     * 执行sql
     *
     * @param sql
     * @param function
     * @return
     */

    public <R> R executeSql(String sql, Function<ResultSet, R> function) {
        if (StringUtils.isEmpty(sql)) {
            return null;
        }
        log.info("execute:{}", sql);
        try (Statement stmt = getConnection().createStatement();) {
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
            TimeInterval timeInterval = new TimeInterval();
            boolean query = stmt.execute(sql.replaceFirst(";", ""));
            executeResult.setDescription("执行成功");
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
                                .name(resultSetMetaData.getColumnName(i))
                                .build());
                    }

                    // 获取数据信息
                    List<List<String>> dataList = Lists.newArrayList();
                    executeResult.setDataList(dataList);

                    while (rs.next()) {
                        List<String> row = Lists.newArrayListWithExpectedSize(col);
                        dataList.add(row);
                        for (int i = 1; i <= col; i++) {
                            row.add(ai.chat2db.spi.util.JdbcUtils.getResultSetValue(rs, i));
                        }
                    }
                    executeResult.setDuration(timeInterval.interval());
                    return executeResult;
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            } else {
                // 修改或者其他
                executeResult.setUpdateCount(stmt.getUpdateCount());
            }
        }
        return executeResult;
    }

    /**
     * 执行sql
     *
     * @param sql
     * @return
     * @throws SQLException
     */
    public ExecuteResult execute(String sql) throws SQLException {
        return execute(sql, getConnection());
    }


    /**
     * 获取所有的数据库
     *
     * @return
     */
    public List<String> databases() {
        List<String> tables = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getCatalogs();) {
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
     * @param databaseName
     * @param schemaName
     * @return
     */
    public List<Map<String, String>> schemas(String databaseName, String schemaName) {
        List<Map<String, String>> schemaList = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getSchemas(databaseName, schemaName)) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", resultSet.getString("TABLE_SCHEM"));
                    map.put("databaseName", resultSet.getString("TABLE_CATALOG"));
                    schemaList.add(map);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return schemaList;
    }

    /**
     * 获取所有的数据库表
     *
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @param types
     * @return
     */
    public List<Table> tables(String databaseName, String schemaName, String tableName, String types[]) {
        List<Table> tables = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getTables(databaseName, schemaName, tableName,
                types)) {
            if (resultSet != null) {
                while (resultSet.next()) {
                    tables.add(buildTable(resultSet));
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
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @param columnName
     * @return
     */
    public List<TableColumn> columns(String databaseName, String schemaName, String tableName, String columnName) {
        List<TableColumn> tableColumns = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getColumns(databaseName, schemaName, tableName,
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
     * @param databaseName
     * @param schemaName
     * @param tableName
     * @return
     */
    public List<TableIndex> indexes(String databaseName, String schemaName, String tableName) {
        List<TableIndex> tableIndices = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getIndexInfo(databaseName, schemaName, tableName, false,
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
     * @param databaseName
     * @param schemaName
     * @return
     */
    public List<ai.chat2db.spi.model.Function> functions(String databaseName,
                                                         String schemaName) {
        List<ai.chat2db.spi.model.Function> functions = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getFunctions(databaseName, schemaName, null);) {
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
     * @param databaseName
     * @param schemaName
     * @return
     */
    public List<Procedure> procedures(String databaseName, String schemaName) {
        List<Procedure> procedures = Lists.newArrayList();
        try (ResultSet resultSet = getConnection().getMetaData().getProcedures(databaseName, schemaName, null)) {
            while (resultSet != null && resultSet.next()) {
                procedures.add(buildProcedure(resultSet));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return procedures;
    }

    public ExecuteResult executePage(String sql, Integer pageNo, Integer pageSize, Connection connection) throws SQLException {
        //sql判空，处理
        Assert.notNull(sql, "sql");
        int offset = (pageNo - 1) * pageSize;
        sql = sql.replaceFirst(";", "");
        log.info("[sql exec] pageNo:{}, pageSize={}, sql:{}", pageNo, pageSize, sql);
        ExecuteResult executeResult = ExecuteResult.builder().sql(sql).pageNo(pageNo).pageSize(pageSize).success(Boolean.TRUE).build();
        TimeInterval timeInterval = new TimeInterval();
        try (Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setFetchSize(EasyToolsConstant.MAX_PAGE_SIZE);
            boolean query = stmt.execute(sql);
            executeResult.setDescription("sql执行成功");
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
                                .name(resultSetMetaData.getColumnName(i))
                                .build());
                    }
                    // 获取数据信息
                    List<List<String>> dataList = Lists.newArrayList();
                    executeResult.setDataList(dataList);
                    // 分页标记
                    executeResult.setHasNextPage(Boolean.FALSE);

                    // 移动到结果集的最后一行，获取数据总条数
                    rs.last();
                    int rowCount = rs.getRow();
                    executeResult.setTotal(rowCount);
                    // 移动到指定的行分页查询
                    rs.absolute(offset);

                    int rsSize = 0;
                    while (rs.next()) {
                        List<String> row = Lists.newArrayListWithExpectedSize(col);
                        dataList.add(row);
                        for (int i = 1; i <= col; i++) {
                            row.add(ai.chat2db.spi.util.JdbcUtils.getResultSetValue(rs, i));
                        }
                        rsSize++;
                        // 到达下一页了
                        if (rsSize >= pageSize) {
                            executeResult.setHasNextPage(Boolean.TRUE);
                            break;
                        }
                    }
                    return executeResult;
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            } else {
                // 修改或者其他
                executeResult.setUpdateCount(stmt.getUpdateCount());
            }
        } finally {
            executeResult.setDuration(timeInterval.interval());
        }
        return executeResult;
    }
}
