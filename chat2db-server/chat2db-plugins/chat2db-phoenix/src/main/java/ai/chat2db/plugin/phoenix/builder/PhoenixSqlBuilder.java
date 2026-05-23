package ai.chat2db.plugin.phoenix.builder;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import ai.chat2db.plugin.phoenix.type.PhoenixColumnTypeEnum;
import ai.chat2db.plugin.phoenix.type.PhoenixIndexTypeEnum;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;

public class PhoenixSqlBuilder extends DefaultSqlBuilder implements SqlBuilder{


    @Override
    public String buildCreateTableSql(Table table) {
        StringBuilder script = new StringBuilder();
        if(StringUtils.isNotBlank(table.getAiComment())){
            script.append(" -- ").append(table.getAiComment()).append("\n");
        }
        script.append("CREATE TABLE ");

        // 添加数据库名
        if (StringUtils.isNotBlank(table.getSchemaName())) {
            script.append(table.getSchemaName()).append(".");
        }
        script.append(table.getName()).append(" (").append("\n");

        // 添加列
        appendColumns(script, table.getColumnList());

        // 添加索引
        appendIndexes(script, table.getIndexList());

        // 移除最后的逗号
        if (script.length() > 2) {
            script.setLength(script.length() - 2);
        }
        script.append("\n)");

        // 添加表的其他属性
        appendTableAttributes(script, table);

        script.append(";");

        return script.toString();
    }

    // 添加列的方法
    @Override
    protected void appendColumns(StringBuilder script, List<TableColumn> columns) {
        for (TableColumn column : columns) {
            String columnType = column.getDataType();
            if (StringUtils.isBlank(columnType)) {
                columnType = column.getColumnType();
            }
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(columnType)) {
                continue;
            }
            PhoenixColumnTypeEnum typeEnum = PhoenixColumnTypeEnum.getByType(columnType);
            script.append("\t").append(typeEnum.buildCreateColumnSql(column)).append(",");
            if(StringUtils.isNotBlank(column.getAiComment())){
                script.append(" -- ").append(column.getAiComment());
            }
            script.append("\n");
        }
    }

    // 添加索引的方法
    @Override
    protected void appendIndexes(StringBuilder script, List<TableIndex> indexes) {
        for (TableIndex tableIndex : indexes) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            PhoenixIndexTypeEnum indexTypeEnum = PhoenixIndexTypeEnum.getByType(tableIndex.getType());
            script.append("\t").append(indexTypeEnum.buildIndexScript(tableIndex)).append(",\n");
        }
    }

    // 添加表的其他属性的方法
    @Override
    protected void appendTableAttributes(StringBuilder script, Table table) {
        if (StringUtils.isNotBlank(table.getEngine())) {
            script.append(" ENGINE=").append(table.getEngine());
        }
        if (StringUtils.isNotBlank(table.getCharset())) {
            script.append(" DEFAULT CHARACTER SET=").append(table.getCharset());
        }
        if (StringUtils.isNotBlank(table.getCollate())) {
            script.append(" COLLATE=").append(table.getCollate());
        }
        if (table.getIncrementValue() != null) {
            script.append(" AUTO_INCREMENT=").append(table.getIncrementValue());
        }
        if (StringUtils.isNotBlank(table.getPartition())) {
            script.append(" \n").append(table.getPartition());
        }
    }


    @Override
    // 修改列的方法
    protected void modifyColumns(StringBuilder script, Table oldTable, Table newTable) {
        for (TableColumn tableColumn : newTable.getColumnList()) {
            String columnType = tableColumn.getDataType();
            if (StringUtils.isBlank(columnType)) {
                columnType = tableColumn.getColumnType();
            }
            if (StringUtils.isNotBlank(tableColumn.getEditStatus()) && StringUtils.isNotBlank(columnType) && StringUtils.isNotBlank(tableColumn.getName())) {
                PhoenixColumnTypeEnum typeEnum = PhoenixColumnTypeEnum.getByType(columnType);
                script.append("\t").append(typeEnum.buildModifyColumn(tableColumn)).append(",\n");
            }
        }
    }
    @Override
    // 修改索引的方法
    protected void modifyIndexes(StringBuilder script, Table oldTable, Table newTable) {
        for (TableIndex tableIndex : newTable.getIndexList()) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus()) && StringUtils.isNotBlank(tableIndex.getType())) {
                PhoenixIndexTypeEnum indexTypeEnum = PhoenixIndexTypeEnum.getByType(tableIndex.getType());
                script.append("\t").append(indexTypeEnum.buildModifyIndex(tableIndex)).append(",\n");
            }
        }
    }


    /**
     * 修改表名和注释的方法
     */
    @Override
    protected void modifyTableNameAndComment(StringBuilder script, Table oldTable, Table newTable) {
        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append("\t").append("RENAME TO ").append("`").append(newTable.getName()).append("`").append(",\n");
        }
        if (!Objects.equals(oldTable.getIncrementValue(), newTable.getIncrementValue())) {
            script.append("\t").append("AUTO_INCREMENT=").append(newTable.getIncrementValue()).append(",\n");
        }
        if(StringUtils.isNotBlank(newTable.getAiComment())){
            script.append(" -- ").append(newTable.getAiComment()).append(",\n");
        }
    }
}
