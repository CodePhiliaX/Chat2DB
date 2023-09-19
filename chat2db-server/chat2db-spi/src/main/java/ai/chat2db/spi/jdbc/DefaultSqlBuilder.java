package ai.chat2db.spi.jdbc;

import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.model.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class DefaultSqlBuilder implements SqlBuilder {
    @Override
    public String generateCreateTableSql(String databaseName, String schemaName, Table table) {
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE ");
        script.append("`").append(table.getName()).append("`").append(" (").append("\n");
        for (ai.chat2db.spi.model.TableColumn column : table.getColumnList()) {
            script.append("`").append(column.getName()).append("`").append(" ");
            script.append(column.getDataType()).append(" ");
            if (column.getColumnSize() != null) {
                script.append("(").append(column.getColumnSize()).append(") ");
            }
//            if (column.getType().getDecimal() != null) {
//                script.append("(").append(column.getType().getLength()).append(",").append(column.getType().getDecimal()).append(") ");
//            }
//            if (column.isNullable()) {
//                script.append("NULL ");
//            } else {
//                script.append("NOT NULL ");
//            }
//            if (column.getDefaultValue() != null) {
//                script.append("DEFAULT ").append(column.getDefaultValue()).append(" ");
//            }
//            if (column.getComment() != null) {
//                script.append("COMMENT '").append(column.getComment()).append("' ");
//            }
            script.append(",").append("\n");
        }
        return null;
    }

    private String generateColumnSql(ai.chat2db.spi.model.TableColumn column) {
        if(StringUtils.isEmpty(column.getColumnType())){
            return "";
        }
        String type = column.getColumnType().toUpperCase();

        StringBuilder script = new StringBuilder();
        script.append("`").append(column.getName()).append("`").append(" ");
        script.append(type).append(" ");
        if(Arrays.asList("","").contains(type)){
            script.append("(").append(column.getColumnSize()).append(") ");
        }




        return script.toString();
    }

    @Override
    public String generateModifyTaleSql(String databaseName, String schemaName, Table newTable, Table oldTable) {
        return null;
    }
}
