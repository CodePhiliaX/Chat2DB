package ai.chat2db.spi.jdbc;

import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.enums.DmlType;
import ai.chat2db.spi.model.*;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultSqlBuilder implements SqlBuilder<Table> {


    @Override
    public String buildCreateTableSql(Table table) {
        return null;
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        return null;
    }

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        return null;
    }

    public static String CREATE_DATABASE_SQL = "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET %s COLLATE %s";

    @Override
    public String buildCreateDatabaseSql(Database database) {
        return null;
    }

    @Override
    public String buildModifyDatabaseSql(Database oldDatabase, Database newDatabase) {
        return null;
    }

    @Override
    public String buildCreateSchemaSql(Schema schema) {
        return null;
    }

    @Override
    public String buildModifySchemaSql(String oldSchemaName, String newSchemaName) {
        return null;
    }

    @Override
    public String buildOrderBySql(String originSql, List<OrderBy> orderByList) {
        if (CollectionUtils.isEmpty(orderByList)) {
            return originSql;
        }
        try {
            Statement statement = CCJSqlParserUtil.parse(originSql);
            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

                // Create a new ORDER BY clause
                List<OrderByElement> orderByElements = new ArrayList<>();

                for (OrderBy orderBy : orderByList) {
                    OrderByElement orderByElement = new OrderByElement();
                    orderByElement.setExpression(CCJSqlParserUtil.parseExpression(orderBy.getColumnName()));
                    orderByElement.setAsc(orderBy.isAsc()); // Set to ascending order, use setAsc(false) to set to descending order
                    orderByElements.add(orderByElement);
                }
                // Replace the original ORDER BY clause
                plainSelect.setOrderByElements(orderByElements);
                // Output the modified SQL
                return plainSelect.toString();
            }
        } catch (Exception e) {
        }
        return originSql;
    }

    @Override
    public String buildSqlByQuery(QueryResult queryResult) {
        List<Header> headerList = queryResult.getHeaderList();
        List<ResultOperation> operations = queryResult.getOperations();
        String tableName = queryResult.getTableName();
        StringBuilder stringBuilder = new StringBuilder();
        MetaData metaSchema = Chat2DBContext.getMetaData();
        List<String> keyColumns = getPrimaryColumns(headerList);
        for (int i = 0; i < operations.size(); i++) {
            ResultOperation operation = operations.get(i);
            List<String> row = operation.getDataList();
            List<String> odlRow = operation.getOldDataList();
            String sql = "";
            if ("UPDATE".equalsIgnoreCase(operation.getType())) {
                sql = getUpdateSql(tableName, headerList, row, odlRow, metaSchema, keyColumns, false);
            } else if ("CREATE".equalsIgnoreCase(operation.getType())) {
                sql = getInsertSql(tableName, headerList, row, metaSchema);
            } else if ("DELETE".equalsIgnoreCase(operation.getType())) {
                sql = getDeleteSql(tableName, headerList, odlRow, metaSchema, keyColumns);
            } else if ("UPDATE_COPY".equalsIgnoreCase(operation.getType())) {
                sql = getUpdateSql(tableName, headerList, row, row, metaSchema, keyColumns, true);
            }

            stringBuilder.append(sql + ";\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String getTableDmlSql(Table table, String type) {
        if (table == null || CollectionUtils.isEmpty(table.getColumnList()) || StringUtils.isBlank(type)) {
            return "";
        }
        if(DmlType.INSERT.name().equalsIgnoreCase(type)) {
            return getInsertSql(table.getName(), table.getColumnList());
        } else if(DmlType.UPDATE.name().equalsIgnoreCase(type)) {
            return getUpdateSql(table.getName(), table.getColumnList());
        } else if(DmlType.DELETE.name().equalsIgnoreCase(type)) {
            return getDeleteSql(table.getName(), table.getColumnList());
        }else if(DmlType.SELECT.name().equalsIgnoreCase(type)) {
            return getSelectSql(table.getName(), table.getColumnList());
        }
        return "";
    }

    private String getSelectSql(String name, List<TableColumn> columnList) {
        StringBuilder script = new StringBuilder();
        script.append("SELECT ");
        for (TableColumn column : columnList) {
            script.append(column.getName())
                    .append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(" FROM where").append(name);
        return script.toString();
    }

    private String getDeleteSql(String name, List<TableColumn> columnList) {
        StringBuilder script = new StringBuilder();
        script.append("DELETE FROM ").append(name)
                .append(" where ");
        return script.toString();
    }

    private String getUpdateSql(String name, List<TableColumn> columnList) {
        StringBuilder script = new StringBuilder();
        script.append("UPDATE ").append(name)
                .append(" set ");
        for (TableColumn column : columnList) {
            script.append(column.getName())
                    .append(" = ")
                    .append(" ")
                    .append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(" where ");
        return script.toString();
    }

    private String getInsertSql(String name, List<TableColumn> columnList) {
        StringBuilder script = new StringBuilder();
        script.append("INSERT INTO ").append(name)
                .append(" (");
        for (TableColumn column : columnList) {
            script.append(column.getName())
                    .append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(") VALUES (");
        for (TableColumn column : columnList) {
            script.append(" ")
                    .append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();
    }

    private List<String> getPrimaryColumns(List<Header> headerList) {
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

    private String getDeleteSql(String tableName, List<Header> headerList, List<String> row, MetaData metaSchema,
                                List<String> keyColumns) {
        StringBuilder script = new StringBuilder();
        script.append("DELETE FROM ").append(tableName).append("");
        script.append(buildWhere(headerList, row, metaSchema, keyColumns));
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

    private String getInsertSql(String tableName, List<Header> headerList, List<String> row, MetaData metaSchema) {
        if (CollectionUtils.isEmpty(row) || ObjectUtils.allNull(row.toArray())) {
            return "";
        }
        StringBuilder script = new StringBuilder();
        script.append("INSERT INTO ").append(tableName)
                .append(" (");
        for (int i = 1; i < row.size(); i++) {
            Header header = headerList.get(i);
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
            Header header = headerList.get(i);
            script.append(SqlUtils.getSqlValue(newValue, header.getDataType()))
                    .append(",");
            //}
        }
        script.deleteCharAt(script.length() - 1);
        script.append(")");
        return script.toString();

    }

    private String getUpdateSql(String tableName, List<Header> headerList, List<String> row, List<String> odlRow,
                                MetaData metaSchema,
                                List<String> keyColumns, boolean copy) {
        StringBuilder script = new StringBuilder();
        if (CollectionUtils.isEmpty(row) || CollectionUtils.isEmpty(odlRow)) {
            return "";
        }
        script.append("UPDATE ").append(tableName).append(" set ");
        for (int i = 1; i < row.size(); i++) {
            String newValue = row.get(i);
            String oldValue = odlRow.get(i);
            if (StringUtils.equals(newValue, oldValue) && !copy) {
                continue;
            }
            Header header = headerList.get(i);
            String newSqlValue = SqlUtils.getSqlValue(newValue, header.getDataType());
            script.append(metaSchema.getMetaDataName(header.getName()))
                    .append(" = ")
                    .append(newSqlValue)
                    .append(",");
        }
        script.deleteCharAt(script.length() - 1);
        script.append(buildWhere(headerList, odlRow, metaSchema, keyColumns));
        return script.toString();
    }
}
