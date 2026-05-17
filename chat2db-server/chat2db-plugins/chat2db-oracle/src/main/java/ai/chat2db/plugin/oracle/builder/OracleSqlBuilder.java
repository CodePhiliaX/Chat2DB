package ai.chat2db.plugin.oracle.builder;

import ai.chat2db.plugin.oracle.type.OracleColumnTypeEnum;
import ai.chat2db.plugin.oracle.type.OracleIndexTypeEnum;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Header;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class OracleSqlBuilder extends DefaultSqlBuilder implements SqlBuilder {
    @Override
    public String buildCreateTableSql(Table table) {
        StringBuilder script = new StringBuilder();

        script.append("CREATE TABLE ").append("\"").append(table.getSchemaName()).append("\".\"").append(table.getName()).append("\" (").append("\n");

        for (TableColumn column : table.getColumnList()) {
            String columnType = column.getDataType();
            if (StringUtils.isBlank(columnType)) {
                columnType = column.getColumnType();
            }
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(columnType)) {
                continue;
            }
            OracleColumnTypeEnum typeEnum = OracleColumnTypeEnum.getByType(columnType);
            script.append("\t").append(typeEnum.buildCreateColumnSql(column)).append(",\n");
        }

        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append("\n);");

        for (TableIndex tableIndex : table.getIndexList()) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            OracleIndexTypeEnum oracleColumnTypeEnum = OracleIndexTypeEnum.getByType(tableIndex.getType());
            script.append("\n").append("").append(oracleColumnTypeEnum.buildIndexScript(tableIndex)).append(";");
        }

        for (TableColumn column : table.getColumnList()) {
            String columnType = column.getDataType();
            if (StringUtils.isBlank(columnType)) {
                columnType = column.getColumnType();
            }
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(columnType) || StringUtils.isBlank(column.getComment())) {
                continue;
            }
            script.append("\n").append(buildComment(column)).append(";");
        }

        if (StringUtils.isNotBlank(table.getComment())) {
            script.append("\n").append(buildTableComment(table)).append(";");
        }


        return script.toString();
    }

    private String buildTableComment(Table table) {
        StringBuilder script = new StringBuilder();
        script.append("COMMENT ON TABLE ").append("\"").append(table.getSchemaName()).append("\".\"").append(table.getName()).append("\" IS '").append(table.getComment()).append("'");
        return script.toString();
    }

    private String buildComment(TableColumn column) {
        StringBuilder script = new StringBuilder();
        script.append("COMMENT ON COLUMN ").append("\"").append(column.getSchemaName()).append("\".\"").append(column.getTableName()).append("\".\"").append(column.getName()).append("\" IS '").append(column.getComment()).append("'");
        return script.toString();
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        StringBuilder script = new StringBuilder();

        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append("ALTER TABLE ").append("\"").append(oldTable.getSchemaName()).append("\".\"").append(oldTable.getName()).append("\"");
            script.append(" ").append("RENAME TO ").append("\"").append(newTable.getName()).append("\"").append(";\n");
        }
        if (!StringUtils.equalsIgnoreCase(oldTable.getComment(), newTable.getComment())) {
            script.append("").append(buildTableComment(newTable)).append(";\n");
        }


        // append modify column
        for (TableColumn tableColumn : newTable.getColumnList()) {
            String columnType = tableColumn.getDataType();
            if (StringUtils.isBlank(columnType)) {
                columnType = tableColumn.getColumnType();
            }
            if (StringUtils.isNotBlank(tableColumn.getEditStatus())) {
                OracleColumnTypeEnum typeEnum = OracleColumnTypeEnum.getByType(columnType);
                script.append("\t").append(typeEnum.buildModifyColumn(tableColumn)).append(";\n");
                if (StringUtils.isNotBlank(tableColumn.getComment())) {
                    script.append("\n").append(buildComment(tableColumn)).append(";\n");
                }
            }
        }

        // append modify index
        for (TableIndex tableIndex : newTable.getIndexList()) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus()) && StringUtils.isNotBlank(tableIndex.getType())) {
                OracleIndexTypeEnum mysqlIndexTypeEnum = OracleIndexTypeEnum.getByType(tableIndex.getType());
                script.append("\t").append(mysqlIndexTypeEnum.buildModifyIndex(tableIndex)).append(";\n");
            }
        }
        if (script.length() > 2) {
            script = new StringBuilder(script.substring(0, script.length() - 2));
            script.append(";");
        }

        return script.toString();
    }

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        int startRow = offset;
        int endRow = offset + pageSize;
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        if (startRow > 0) {
            sqlBuilder.append("SELECT * FROM ( ");
        }
        if (endRow > 0) {
            sqlBuilder.append(" SELECT TMP_PAGE.*, ROWNUM CAHT2DB_AUTO_ROW_ID FROM ( ");
        }
        sqlBuilder.append("\n");
        sqlBuilder.append(sql);
        sqlBuilder.append("\n");
        if (endRow > 0) {
            sqlBuilder.append(" ) TMP_PAGE WHERE ROWNUM <= ");
            sqlBuilder.append(endRow);
        }
        if (startRow > 0) {
            sqlBuilder.append(" ) WHERE CAHT2DB_AUTO_ROW_ID > ");
            sqlBuilder.append(startRow);
        }
        return sqlBuilder.toString();
    }

    @Override
    protected String buildImportUpsertSql(String tableName, List<Header> headerList, List<String> primaryKeyColumns,
                                          MetaData metaSchema) {
        if (primaryKeyColumns == null || primaryKeyColumns.isEmpty()) {
            return buildImportInsertSql(tableName, headerList, metaSchema);
        }
        StringBuilder sql = new StringBuilder("MERGE INTO ");
        sql.append(tableName).append(" t USING (SELECT ");
        for (int i = 0; i < headerList.size(); i++) {
            if (i > 0) sql.append(",");
            sql.append("? AS ").append(metaSchema.getMetaDataName(headerList.get(i).getName()));
        }
        sql.append(" FROM DUAL) s ON (");
        for (int i = 0; i < primaryKeyColumns.size(); i++) {
            if (i > 0) sql.append(" AND ");
            String pk = metaSchema.getMetaDataName(primaryKeyColumns.get(i));
            sql.append("t.").append(pk).append("=s.").append(pk);
        }
        sql.append(") WHEN MATCHED THEN UPDATE SET ");
        boolean first = true;
        for (Header header : headerList) {
            if (primaryKeyColumns.contains(header.getName())) {
                continue;
            }
            if (!first) sql.append(",");
            String quotedName = metaSchema.getMetaDataName(header.getName());
            sql.append("t.").append(quotedName).append("=s.").append(quotedName);
            first = false;
        }
        sql.append(" WHEN NOT MATCHED THEN INSERT (");
        first = true;
        for (Header header : headerList) {
            if (!first) sql.append(",");
            sql.append(metaSchema.getMetaDataName(header.getName()));
            first = false;
        }
        sql.append(") VALUES (");
        first = true;
        for (Header header : headerList) {
            if (!first) sql.append(",");
            sql.append("s.").append(metaSchema.getMetaDataName(header.getName()));
            first = false;
        }
        sql.append(")");
        return sql.toString();
    }
}
