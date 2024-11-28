package ai.chat2db.plugin.duckdb.builder;

import ai.chat2db.plugin.duckdb.type.DuckDBColumnTypeEnum;
import ai.chat2db.plugin.duckdb.type.DuckDBIndexTypeEnum;
import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Database;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import ai.chat2db.spi.util.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class DuckDBSqlBuilder extends DefaultSqlBuilder {
    @Override
    public String buildCreateTableSql(Table table) {
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE ");
        if (StringUtils.isNotBlank(table.getSchemaName())) {
            script.append(table.getSchemaName()).append(".");
        }
        script.append(table.getName()).append(" (").append("\n");

        // append column
        for (TableColumn column : table.getColumnList()) {
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(column.getColumnType())) {
                continue;
            }
            DuckDBColumnTypeEnum typeEnum = DuckDBColumnTypeEnum.getByType(column.getColumnType());
            if (typeEnum == null) {
                continue;
            }
            script.append("\t").append(typeEnum.buildCreateColumnSql(column)).append(",\n");
        }

        // append primary key and index
        for (TableIndex tableIndex : table.getIndexList()) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            DuckDBIndexTypeEnum mysqlIndexTypeEnum = DuckDBIndexTypeEnum.getByType(tableIndex.getType());
            if (mysqlIndexTypeEnum == null) {
                continue;
            }
            script.append("\t").append(mysqlIndexTypeEnum.buildCreateIndexScript(tableIndex)).append(",\n");
        }

        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append("\n);\n");


        if (StringUtils.isNotBlank(table.getComment())) {
            script.append(" COMMENT ON TABLE ").append(table.getSchemaName()).append(".").append(table.getName())
                    .append(" IS '").append(table.getComment()).append("'");
        }

        script.append(";");

        return script.toString();
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        StringBuilder tableBuilder = new StringBuilder();

        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            tableBuilder.append("ALTER TABLE ").append(oldTable.getSchemaName()).append(".").append(oldTable.getName())
                    .append(" RENAME TO ").append("'").append(newTable.getName()).append("'").append(";\n");
        }

        if (!StringUtils.equalsIgnoreCase(oldTable.getComment(), newTable.getComment())) {
            tableBuilder.append("COMMENT ON TABLE ").append(oldTable.getSchemaName()).append(".").append(oldTable.getName())
                    .append(" IS ").append("'").append(newTable.getComment()).append("'").append(";\n");
        }


        // append modify column
        for (TableColumn tableColumn : newTable.getColumnList()) {
            if ((StringUtils.isNotBlank(tableColumn.getEditStatus()) && StringUtils.isNotBlank(tableColumn.getColumnType())
                    && StringUtils.isNotBlank(tableColumn.getName()))) {
                DuckDBColumnTypeEnum typeEnum = DuckDBColumnTypeEnum.getByType(tableColumn.getColumnType());
                if (typeEnum == null) {
                    continue;
                }
                tableBuilder.append("\t").append(typeEnum.buildModifyColumn(tableColumn)).append("\n");

            }
        }

        // append modify index
        for (TableIndex tableIndex : newTable.getIndexList()) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus()) && StringUtils.isNotBlank(tableIndex.getType())) {
                DuckDBIndexTypeEnum duckDBIndexTypeEnum = DuckDBIndexTypeEnum.getByType(tableIndex.getType());
                if (duckDBIndexTypeEnum == null) {
                    continue;
                }
                tableBuilder.append("\t").append(duckDBIndexTypeEnum.buildModifyIndex(tableIndex)).append(";\n");
            }
        }

        // append reorder column
        // script.append(buildGenerateReorderColumnSql(oldTable, newTable));

        if (tableBuilder.length() > 2) {
            tableBuilder = new StringBuilder(tableBuilder.substring(0, tableBuilder.length() - 2));
            tableBuilder.append(";");
            return tableBuilder.toString();
        } else {
            return StringUtils.EMPTY;
        }

    }

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 14);
        sqlBuilder.append(sql);
        if (offset == 0) {
            sqlBuilder.append("\n LIMIT ");
            sqlBuilder.append(pageSize);
        } else {
            sqlBuilder.append("\n LIMIT ");
            sqlBuilder.append(offset);
            sqlBuilder.append(",");
            sqlBuilder.append(pageSize);
        }
        return sqlBuilder.toString();
    }


    @Override
    public String buildCreateDatabaseSql(Database database) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE DATABASE " + database.getName());
        if (StringUtils.isNotBlank(database.getCharset())) {
            sqlBuilder.append(" DEFAULT CHARACTER SET=").append(database.getCharset());
        }
        if (StringUtils.isNotBlank(database.getCollation())) {
            sqlBuilder.append(" COLLATE=").append(database.getCollation());
        }
        return sqlBuilder.toString();
    }


    @Override
    protected void buildTableName(String databaseName, String schemaName, String tableName, StringBuilder script) {
        if (StringUtils.isNotBlank(databaseName)) {
            script.append(SqlUtils.quoteObjectName(databaseName, "'")).append('.');
        }
        if (StringUtils.isNotBlank(schemaName)) {
            script.append(SqlUtils.quoteObjectName(schemaName, "'")).append('.');
        }
        script.append(SqlUtils.quoteObjectName(tableName, "'"));
    }

    /**
     * @param columnList
     * @param script
     */
    @Override
    protected void buildColumns(List<String> columnList, StringBuilder script) {
        if (CollectionUtils.isNotEmpty(columnList)) {
            script.append(" (")
                    .append(columnList.stream().map(s -> SqlUtils.quoteObjectName(s, "`")).collect(Collectors.joining(",")))
                    .append(") ");
        }
    }

}
