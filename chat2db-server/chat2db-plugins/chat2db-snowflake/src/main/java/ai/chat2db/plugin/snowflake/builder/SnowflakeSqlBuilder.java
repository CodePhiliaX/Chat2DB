package ai.chat2db.plugin.snowflake.builder;
import ai.chat2db.plugin.snowflake.type.SnowflakeColumnTypeEnum;
import ai.chat2db.plugin.snowflake.type.SnowflakeIndexTypeEnum;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import org.apache.commons.lang3.StringUtils;

public class SnowflakeSqlBuilder extends DefaultSqlBuilder implements SqlBuilder {

    @Override
    public String buildCreateTableSql(Table table){
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE ");
        if (StringUtils.isNotBlank(table.getSchemaName())) {
            script.append(table.getSchemaName()).append(".");
        }
        script.append("\"").append(table.getName()).append("\"").append(" (").append("\n");

        // append column
        for (TableColumn column : table.getColumnList()) {
            if (StringUtils.isBlank(column.getName()) || StringUtils.isBlank(column.getColumnType())) {
                continue;
            }
            SnowflakeColumnTypeEnum typeEnum = SnowflakeColumnTypeEnum.getByType(column.getColumnType());
            script.append("\t").append(typeEnum.buildCreateColumnSql(column)).append(",\n");
        }

        // append primary key and index
        for (TableIndex tableIndex : table.getIndexList()) {
            if (StringUtils.isBlank(tableIndex.getName()) || StringUtils.isBlank(tableIndex.getType())) {
                continue;
            }
            SnowflakeIndexTypeEnum mysqlIndexTypeEnum = SnowflakeIndexTypeEnum.getByType(tableIndex.getType());
            script.append("\t").append("").append(mysqlIndexTypeEnum.buildIndexScript(tableIndex)).append(",\n");
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
        script.append("ALTER TABLE ");
        script.append("\"").append(oldTable.getName()).append("\"").append("\n");
        boolean isChangeTableName = false;
        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append("RENAME TO ").append("\"").append(newTable.getName()).append("\"").append(";\n");
            isChangeTableName = true;
        }
        if (!StringUtils.equalsIgnoreCase(oldTable.getComment(), newTable.getComment())) {
            if (isChangeTableName) {
                script.append("ALTER TABLE ");
                script.append("\"").append(newTable.getName()).append("\"").append("\n");
                script.append("\t").append("set COMMENT=").append("'").append(newTable.getComment()).append("'").append(",\n");
            } else {
                script.append("\t").append("set COMMENT=").append("'").append(newTable.getComment()).append("'").append(",\n");
            }
        }
        if (oldTable.getIncrementValue() != newTable.getIncrementValue()) {
            script.append("\t").append("AUTO_INCREMENT=").append(newTable.getIncrementValue()).append(",\n");
        }

        // append modify column
        for (TableColumn tableColumn : newTable.getColumnList()) {
            if (StringUtils.isNotBlank(tableColumn.getEditStatus()) && StringUtils.isNotBlank(tableColumn.getColumnType()) && StringUtils.isNotBlank(tableColumn.getName())) {
                SnowflakeColumnTypeEnum typeEnum = SnowflakeColumnTypeEnum.getByType(tableColumn.getColumnType());
                script.append("\t").append(typeEnum.buildModifyColumn(tableColumn)).append(",\n");
            }
        }

        // append reorder column
        //script.append(buildGenerateReorderColumnSql(oldTable, newTable));

        if (script.length() > 2) {
            script = new StringBuilder(script.substring(0, script.length() - 2));
            script.append(";");
        }

        return script.toString();
    }

}

