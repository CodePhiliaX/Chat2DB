package ai.chat2db.server.domain.core.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import ai.chat2db.server.domain.api.param.*;
import ai.chat2db.server.domain.api.param.operation.OperationLogCreateParam;
import ai.chat2db.server.domain.api.service.OperationLogService;
import ai.chat2db.server.domain.api.service.TableService;
import ai.chat2db.server.domain.core.util.MetaNameUtils;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.ValueHandler;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.ConnectInfo;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.ParserException;

import ai.chat2db.server.domain.api.service.DlTemplateService;
import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.ListResult;
import ai.chat2db.server.tools.common.util.EasyCollectionUtils;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.enums.DataTypeEnum;
import ai.chat2db.spi.enums.SqlTypeEnum;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.SQLExecutor;
import ai.chat2db.spi.util.JdbcUtils;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author moji
 * @version DataSourceCoreServiceImpl.java, v 0.1 2022年09月23日 15:51 moji Exp $
 * @date 2022/09/23
 */
@Slf4j
@Service
public class DlTemplateServiceImpl implements DlTemplateService {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private TableService tableService;

    @Override
    public ListResult<ExecuteResult> execute(DlExecuteParam param) {
        if (StringUtils.isBlank(param.getSql())) {
            return ListResult.empty();
        }
        // 解析sql
        String type = Chat2DBContext.getConnectInfo().getDbType();
        DbType dbType = JdbcUtils.parse2DruidDbType(type);
        if ("SQLSERVER".equalsIgnoreCase(type)) {
            RemoveSpecialGO(param);
        }

        List<String> sqlList = SqlUtils.parse(param.getSql(), dbType);


        if (CollectionUtils.isEmpty(sqlList)) {
            throw new BusinessException("dataSource.sqlAnalysisError");
        }

        List<ExecuteResult> result = new ArrayList<>();
        ListResult<ExecuteResult> listResult = ListResult.of(result);
        // 执行sql
        for (String originalSql : sqlList) {
            ExecuteResult executeResult = executeSQL(originalSql, dbType, param);
            result.add(executeResult);
            if (!executeResult.getSuccess()) {
                listResult.setSuccess(false);
                listResult.errorCode(executeResult.getDescription());
                listResult.setErrorMessage(executeResult.getMessage());
            }
            addOperationLog(executeResult);
        }
        return listResult;
    }

    @Override
    public DataResult<ExecuteResult> executeUpdate(DlExecuteParam param) {
        DataResult<ExecuteResult> dataResult = new DataResult<>();
        dataResult.setSuccess(true);
        RemoveSpecialGO(param);
        DbType dbType =
                JdbcUtils.parse2DruidDbType(Chat2DBContext.getConnectInfo().getDbType());
        List<String> sqlList = SqlUtils.parse(param.getSql(), dbType);
        Connection connection = Chat2DBContext.getConnection();
        try {
            connection.setAutoCommit(false);
            for (String originalSql : sqlList) {
                ExecuteResult executeResult = SQLExecutor.getInstance().executeUpdate(originalSql, connection, 1);
                dataResult.setData(executeResult);
                addOperationLog(executeResult);
            }
            connection.commit();
        } catch (Exception e) {
            log.error("executeUpdate error", e);
            dataResult.setSuccess(false);
            dataResult.setErrorCode("connection error");
            dataResult.setErrorMessage(e.getMessage());
        }
        return dataResult;
    }

    private void RemoveSpecialGO(DlExecuteParam param) {
        String sql = param.getSql();
        if (StringUtils.isBlank(sql)) {
            return;
        }
        sql = sql.replaceAll("(?mi)^[ \\t]*go[ \\t]*$", ";");
        param.setSql(sql);
    }


    private ExecuteResult executeSQL(String originalSql, DbType dbType, DlExecuteParam param) {
        int pageNo = 1;
        int pageSize = 0;
        Integer offset = null;
        Integer count = null;
        String sqlType = SqlTypeEnum.UNKNOWN.getCode();

        // 解析sql分页
        SQLStatement sqlStatement;
        try {
            sqlStatement = SQLUtils.parseSingleStatement(originalSql, dbType);
            // 是否需要代码帮忙分页
            if (sqlStatement instanceof SQLSelectStatement) {
                pageNo = Optional.ofNullable(param.getPageNo()).orElse(1);
                pageSize = Optional.ofNullable(param.getPageSize()).orElse(EasyToolsConstant.MAX_PAGE_SIZE);
                offset = (pageNo - 1) * pageSize;
                count = pageSize;
                sqlType = SqlTypeEnum.SELECT.getCode();
            }
        } catch (ParserException e) {
            log.warn("解析sql失败:{}", originalSql, e);
        }
        ExecuteResult executeResult = null;
        if (SqlTypeEnum.SELECT.getCode().equals(sqlType) && !SqlUtils.hasPageLimit(originalSql, dbType)) {
            String pageLimit = Chat2DBContext.getSqlBuilder().pageLimit(originalSql, offset, pageNo, pageSize);
            if (StringUtils.isNotBlank(pageLimit)) {
                executeResult = execute(pageLimit, 0, count);
            }
        }
        if (executeResult == null || !executeResult.getSuccess()) {
            executeResult = execute(originalSql, offset, count);
        }


        executeResult.setSqlType(sqlType);
        executeResult.setOriginalSql(originalSql);
        try {
            SqlUtils.buildCanEditResult(originalSql, dbType, executeResult);
        } catch (Exception e) {
            log.warn("buildCanEditResult error", e);
        }
        if (SqlTypeEnum.SELECT.getCode().equals(sqlType)) {
            executeResult.setPageNo(pageNo);
            executeResult.setPageSize(pageSize);
            executeResult.setHasNextPage(
                    CollectionUtils.size(executeResult.getDataList()) >= executeResult.getPageSize());
        } else {
            executeResult.setPageNo(pageNo);
            executeResult.setPageSize(CollectionUtils.size(executeResult.getDataList()));
            executeResult.setHasNextPage(Boolean.FALSE);
        }

        List<Header> headers = executeResult.getHeaderList();
        if (executeResult.getSuccess() && executeResult.isCanEdit() && CollectionUtils.isNotEmpty(headers)){
            headers = setColumnInfo(headers, executeResult.getTableName(), param.getSchemaName(), param.getDatabaseName());
        }
        Header rowNumberHeader = Header.builder()
                .name(I18nUtils.getMessage("sqlResult.rowNumber"))
                .dataType(DataTypeEnum.CHAT2DB_ROW_NUMBER
                        .getCode()).build();

        executeResult.setHeaderList(EasyCollectionUtils.union(Arrays.asList(rowNumberHeader), headers));
        if (executeResult.getDataList() != null) {
            int rowNumberIncrement = 1 + Math.max(pageNo - 1, 0) * pageSize;
            for (int i = 0; i < executeResult.getDataList().size(); i++) {
                List<String> row = executeResult.getDataList().get(i);
                List<String> newRow = Lists.newArrayListWithExpectedSize(row.size() + 1);
                newRow.add(Integer.toString(i + rowNumberIncrement));
                newRow.addAll(row);
                executeResult.getDataList().set(i, newRow);
            }
        }
        //  Total number of fuzzy rows
        executeResult.setFuzzyTotal(calculateFuzzyTotal(pageNo, pageSize, executeResult));
        return executeResult;
    }

    private String calculateFuzzyTotal(int pageNo, int pageSize, ExecuteResult executeResult) {
        int dataSize = CollectionUtils.size(executeResult.getDataList());
        if (pageSize <= 0) {
            return Integer.toString(dataSize);
        }
        int fuzzyTotal = Math.max(pageNo - 1, 0) * pageSize + dataSize;
        if (dataSize < pageSize) {
            return Integer.toString(fuzzyTotal);
        }
        return Integer.toString(fuzzyTotal) + "+";
    }

    @Override
    public DataResult<Long> count(DlCountParam param) {
        if (StringUtils.isBlank(param.getSql())) {
            return DataResult.of(0L);
        }
        DbType dbType =
                JdbcUtils.parse2DruidDbType(Chat2DBContext.getConnectInfo().getDbType());
        String sql = param.getSql();
        // 解析sql分页
        SQLStatement sqlStatement = SQLUtils.parseSingleStatement(sql, dbType);
        if (!(sqlStatement instanceof SQLSelectStatement)) {
            throw new BusinessException("dataSource.sqlAnalysisError");
        }
        sql = PagerUtils.count(sql, dbType);
        ExecuteResult executeResult = execute(sql, null, null);

        List<List<String>> dataList = executeResult.getDataList();
        if (CollectionUtils.isEmpty(dataList)) {
            return DataResult.of(0L);
        }
        String count = EasyCollectionUtils.stream(executeResult.getDataList())
                .findFirst()
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .orElse("0");
        return DataResult.of(Long.valueOf(count));
    }

    @Override
    public DataResult<String> updateSelectResult(UpdateSelectResultParam param) {
        StringBuilder stringBuilder = new StringBuilder();
        MetaData metaSchema = Chat2DBContext.getMetaData();
        List<String> keyColumns = getPrimaryColumns(param);
        for (int i = 0; i < param.getOperations().size(); i++) {
            SelectResultOperation operation = param.getOperations().get(i);
            List<String> row = operation.getDataList();
            List<String> odlRow = operation.getOldDataList();
            String sql = "";
            if ("UPDATE".equalsIgnoreCase(operation.getType())) {
                sql = getUpdateSql(param, row, odlRow, metaSchema, keyColumns, false);
            } else if ("CREATE".equalsIgnoreCase(operation.getType())) {
                sql = getInsertSql(param, row, metaSchema);
            } else if ("DELETE".equalsIgnoreCase(operation.getType())) {
                sql = getDeleteSql(param, odlRow, metaSchema, keyColumns);
            } else if ("UPDATE_COPY".equalsIgnoreCase(operation.getType())) {
                sql = getUpdateSql(param, row, row, metaSchema, keyColumns, true);
            }

            stringBuilder.append(sql + ";\n");
        }
        return DataResult.of(stringBuilder.toString());
    }

    private List<String> getPrimaryColumns(UpdateSelectResultParam param) {
        List<Header> headerList = param.getHeaderList();
        if (CollectionUtils.isEmpty(headerList)) {
            return Lists.newArrayList();
        }
        List<String> keyColumns = Lists.newArrayList();
        for (Header header : headerList) {
            if (header.getPrimaryKey() != null && header.getPrimaryKey()) {
                keyColumns.add(header.getName());
            }
        }
        return keyColumns;
    }

    private String getDeleteSql(UpdateSelectResultParam param, List<String> row, MetaData metaSchema, List<String> keyColumns) {
        StringBuilder script = new StringBuilder();
        script.append("DELETE FROM ").append(param.getTableName()).append("");

        script.append(buildWhere(param.getHeaderList(), row, metaSchema, keyColumns));
        return script.toString();
    }

    private String buildWhere(List<Header> headerList, List<String> row, MetaData metaSchema, List<String> keyColumns) {
        StringBuilder script = new StringBuilder();
        script.append(" where ");
        if (CollectionUtils.isEmpty(keyColumns)) {
            for (int i = 1; i < row.size(); i++) {
                String oldValue = row.get(i);
                Header header = headerList.get(i);
                String value = SqlUtils.getSqlValue(oldValue, header.getDataType());
                if (value == null) {
                    script.append(metaSchema.getMetaDataName(header.getName()))
                            .append(" is null and ");
                } else {
                    script.append(metaSchema.getMetaDataName(header.getName()))
                            .append(" = ")
                            .append(value)
                            .append(" and ");
                }
            }
        } else {
            for (int i = 1; i < row.size(); i++) {
                String oldValue = row.get(i);
                Header header = headerList.get(i);
                String columnName = header.getName();
                if (keyColumns.contains(columnName)) {
                    String value = SqlUtils.getSqlValue(oldValue, header.getDataType());
                    if (value == null) {
                        script.append(metaSchema.getMetaDataName(columnName))
                                .append(" is null and ");
                    } else {
                        script.append(metaSchema.getMetaDataName(columnName))
                                .append(" = ")
                                .append(value)
                                .append(" and ");
                    }
                }
            }
        }
        script.delete(script.length() - 4, script.length());
        return script.toString();
    }

    private String getInsertSql(UpdateSelectResultParam param, List<String> row, MetaData metaSchema) {
        if (CollectionUtils.isEmpty(row) || ObjectUtils.allNull(row.toArray())) {
            return "";
        }
        StringBuilder script = new StringBuilder();
        script.append("INSERT INTO ").append(param.getTableName())
                .append(" (");
        for (int i = 1; i < row.size(); i++) {
            Header header = param.getHeaderList().get(i);
            //String newValue = row.get(i);
            //if (newValue != null) {
                script.append(metaSchema.getMetaDataName(header.getName()))
                        .append(",");
           // }
        }
        script.deleteCharAt(script.length() - 1);
        script.append(") VALUES (");
        for (int i = 1; i < row.size(); i++) {
            String newValue = row.get(i);
            //if (newValue != null) {
                Header header = param.getHeaderList().get(i);
                script.append(SqlUtils.getSqlValue(newValue, header.getDataType()))
                        .append(",");
            //}
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();

    }


    private String getUpdateSql(UpdateSelectResultParam param, List<String> row, List<String> odlRow, MetaData metaSchema,
                                List<String> keyColumns, boolean copy) {
        StringBuilder script = new StringBuilder();
        if (CollectionUtils.isEmpty(row) || CollectionUtils.isEmpty(odlRow)) {
            return "";
        }
        script.append("UPDATE ").append(param.getTableName()).append(" set ");
        for (int i = 1; i < row.size(); i++) {
            String newValue = row.get(i);
            String oldValue = odlRow.get(i);
            if (StringUtils.equals(newValue, oldValue) && !copy) {
                continue;
            }
            Header header = param.getHeaderList().get(i);
            String newSqlValue = SqlUtils.getSqlValue(newValue, header.getDataType());
            script.append(metaSchema.getMetaDataName(header.getName()))
                    .append(" = ")
                    .append(newSqlValue)
                    .append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(buildWhere(param.getHeaderList(), odlRow, metaSchema, keyColumns));
        return script.toString();
    }

    private List<Header> setColumnInfo(List<Header> headers, String tableName, String schemaName, String databaseName) {
        try {
            TableQueryParam tableQueryParam = new TableQueryParam();
            tableQueryParam.setTableName(MetaNameUtils.getMetaName(tableName));
            tableQueryParam.setSchemaName(schemaName);
            tableQueryParam.setDatabaseName(databaseName);
            tableQueryParam.setRefresh(true);
            List<TableColumn> columns = tableService.queryColumns(tableQueryParam);
            if (CollectionUtils.isEmpty(columns)) {
                return headers;
            }
            Map<String, TableColumn> columnMap = columns.stream().collect(Collectors.toMap(TableColumn::getName, tableColumn -> tableColumn));
            List<TableIndex> tableIndices = tableService.queryIndexes(tableQueryParam);
            if (!CollectionUtils.isEmpty(tableIndices)) {
                for (TableIndex tableIndex : tableIndices) {
                    if ("PRIMARY".equalsIgnoreCase(tableIndex.getType())) {
                        List<TableIndexColumn> columnList = tableIndex.getColumnList();
                        if (!CollectionUtils.isEmpty(columnList)) {
                            for (TableIndexColumn tableIndexColumn : columnList) {
                                TableColumn tableColumn = columnMap.get(tableIndexColumn.getColumnName());
                                if (tableColumn != null) {
                                    tableColumn.setPrimaryKey(true);
                                }
                            }
                        }
                    }
                }
            }
            for (Header header : headers) {
                TableColumn tableColumn = columnMap.get(header.getName());
                if (tableColumn != null) {
                    header.setPrimaryKey(tableColumn.getPrimaryKey());
                    header.setComment(tableColumn.getComment());
                    header.setDefaultValue(tableColumn.getDefaultValue());
                    header.setNullable(tableColumn.getNullable());
                    header.setColumnSize(tableColumn.getColumnSize());
                    header.setDecimalDigits(tableColumn.getDecimalDigits());
                }
            }

        }catch (Exception e){
            log.error("setColumnInfo error:",e);
        }
        return headers;
    }

    private ExecuteResult execute(String sql, Integer offset, Integer count) {
        ExecuteResult executeResult;
        try {
            ValueHandler valueHandler = Chat2DBContext.getMetaData().getValueHandler();
            executeResult = SQLExecutor.getInstance().execute(sql, Chat2DBContext.getConnection(), true, offset, count,valueHandler);
        } catch (SQLException e) {
            log.warn("执行sql:{}异常", sql, e);
            executeResult = ExecuteResult.builder()
                    .sql(sql)
                    .success(Boolean.FALSE)
                    .message(e.getMessage())
                    .build();
        }
        return executeResult;
    }

    private void addOperationLog(ExecuteResult executeResult) {
        if (executeResult == null) {
            return;
        }
        try {
            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
            OperationLogCreateParam createParam = new OperationLogCreateParam();
            createParam.setDdl(executeResult.getSql());
            createParam.setStatus(executeResult.getSuccess() ? "success" : "fail");
            createParam.setDatabaseName(connectInfo.getDatabaseName());
            createParam.setDataSourceId(connectInfo.getDataSourceId());
            createParam.setSchemaName(connectInfo.getSchemaName());
            createParam.setUseTime(executeResult.getDuration());
            createParam.setType(connectInfo.getDbType());
            createParam.setOperationRows(executeResult.getUpdateCount() != null ? Long.valueOf(executeResult.getUpdateCount()) : null);
            operationLogService.create(createParam);
        } catch (Exception e) {
            log.error("addOperationLog error:", e);
        }
    }
}
