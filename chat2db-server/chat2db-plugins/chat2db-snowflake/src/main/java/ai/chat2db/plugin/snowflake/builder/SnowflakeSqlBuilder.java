package ai.chat2db.plugin.snowflake.builder;
import ai.chat2db.plugin.snowflake.type.SnowflakeColumnTypeEnum;
import ai.chat2db.spi.SqlBuilder;
import ai.chat2db.spi.jdbc.DefaultSqlBuilder;
import ai.chat2db.spi.model.Table;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.model.TableIndex;
import org.apache.commons.lang3.StringUtils;

public class SnowflakeSqlBuilder extends DefaultSqlBuilder implements SqlBuilder {

    @Override
    public String buildModifyTaleSql(Table oldTable, Table newTable) {
        StringBuilder script = new StringBuilder();
        script.append("ALTER TABLE ");
        script.append(oldTable.getName()).append("\n");
        boolean isChangeTableName = false;
        if (!StringUtils.equalsIgnoreCase(oldTable.getName(), newTable.getName())) {
            script.append("RENAME TO ").append(newTable.getName()).append(";\n");
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

