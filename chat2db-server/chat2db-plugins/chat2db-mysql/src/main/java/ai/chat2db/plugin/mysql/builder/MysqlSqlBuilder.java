package ai.chat2db.plugin.mysql.builder;

import ai.chat2db.plugin.mysql.type.MysqlColumnTypeEnum;
import ai.chat2db.plugin.mysql.type.MysqlIndexTypeEnum;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import org.apache.commons.lang3.StringUtils;


public class MysqlSqlBuilder implements SqlBuilder {
    @Override
    public String buildCreateTableSql(Table table) {
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE ");
        script.append("`").append(table.getName()).append("`").append(" (").append("\n");

        // append column
        for (TableColumn column : table.getColumnList()) {
            MysqlColumnTypeEnum typeEnum = MysqlColumnTypeEnum.getByType(column.getColumnType());
            script.append("\t").append(typeEnum.buildCreateColumnSql(column)).append(",\n");
        }

        // append primary key and index
        for (TableIndex tableIndex : table.getIndexList()) {
            MysqlIndexTypeEnum mysqlIndexTypeEnum = MysqlIndexTypeEnum.getByType(tableIndex.getType());
            script.append("\t").append(mysqlIndexTypeEnum.buildIndexScript(tableIndex)).append(",\n");
        }

        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append("\n)");


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

        if (StringUtils.isNotBlank(table.getComment())) {
            script.append(" COMMENT='").append(table.getComment()).append("'");
        }

        if (StringUtils.isNotBlank(table.getPartition())) {
            script.append(" \n").append(table.getPartition());
        }
        script.append(";");

        return script.toString();
    }

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        StringBuilder script = new StringBuilder();
        script.append("ALTER TABLE ").append("`").append(oldTable.getName()).append("`").append("\n");
        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append("\t").append("RENAME TO ").append("`").append(newTable.getName()).append("`").append(",\n");
        }
        if (!StringUtils.equalsIgnoreCase(oldTable.getComment(), newTable.getComment())) {
            script.append("\t").append("COMMENT=").append("'").append(newTable.getComment()).append("'").append(",\n");
        }
        if (oldTable.getIncrementValue() != newTable.getIncrementValue()) {
            script.append("\t").append("AUTO_INCREMENT=").append(newTable.getIncrementValue()).append(",\n");
        }

        // append modify column
        for (TableColumn tableColumn : newTable.getColumnList()) {
            if (StringUtils.isNotBlank(tableColumn.getEditStatus())) {
                MysqlColumnTypeEnum typeEnum = MysqlColumnTypeEnum.getByType(tableColumn.getColumnType());
                script.append("\t").append(typeEnum.buildModifyColumn(tableColumn)).append(",\n");
            }
        }

        // append modify index
        for (TableIndex tableIndex : newTable.getIndexList()) {
            if (StringUtils.isNotBlank(tableIndex.getEditStatus())) {
                MysqlIndexTypeEnum mysqlIndexTypeEnum = MysqlIndexTypeEnum.getByType(tableIndex.getType());
                script.append("\t").append(mysqlIndexTypeEnum.buildModifyIndex(tableIndex)).append(",\n");
            }
        }

        script = new StringBuilder(script.substring(0, script.length() - 2));
        script.append(";");

        return script.toString();
    }


}
