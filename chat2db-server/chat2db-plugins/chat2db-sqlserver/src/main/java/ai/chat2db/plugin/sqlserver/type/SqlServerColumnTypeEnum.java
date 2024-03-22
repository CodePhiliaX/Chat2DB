package ai.chat2db.plugin.sqlserver.type;

import ai.chat2db.spi.ColumnBuilder;
import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.ColumnType;
import ai.chat2db.spi.model.TableColumn;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum SqlServerColumnTypeEnum implements ColumnBuilder {
    //JSON("JSON", false, false, true, false, false, false, true, false, false, false)

    BIGINT("BIGINT", false, false, true, false, false, false, true, true),

    BINARY("BINARY", false, false, true, false, false, false, true, true),

    BIT("BIT", false, false, true, false, false, false, true, true),

    CHAR("CHAR", true, false, true, false, false, true, true, true),

    DATE("DATE", false, false, true, false, false, false, true, true),

    DATETIME("DATETIME", false, false, true, false, false, false, true, true),

    DATETIME2("DATETIME2", true, false, true, false, false, false, true, true),


    DATETIMEOFFSET("DATETIMEOFFSET", true, false, true, false, false, false, true, true),


    DECIMAL("DECIMAL", true, true, true, false, false, false, true, true),


    FLOAT("FLOAT", true, false, true, false, false, false, true, true),


    GEOGRAPHY("GEOGRAPHY", false, false, true, false, false, false, true, true),

    GEOMETRY("GEOMETRY", false, false, true, false, false, false, true, true),

    HIERARCHYID("HIERARCHYID", false, false, true, false, false, false, true, true),

    IMAGE("IMAGE", false, false, true, false, false, false, true, true),

    INT("INT", false, false, true, false, false, false, true, true),


    MONEY("MONEY", false, false, true, false, false, false, true, true),

    NCHAR("NCHAR", true, false, true, false, false, true, true, true),

    NTEXT("NTEXT", false, false, true, false, false, false, true, true),

    NUMERIC("NUMERIC", true, true, true, false, false, false, true, true),

    NVARCHAR("NVARCHAR", true, false, true, false, false, true, true, true),

    NVARCHAR_MAX("NVARCHAR(MAX)", false, false, true, false, false, true, true, true),


    REAL("REAL", false, false, true, false, false, false, true, true),

    SMALLDATETIME("SMALLDATETIME", false, false, true, false, false, false, true, true),

    SMALLINT("SMALLINT", false, false, true, false, false, false, true, true),

    SMALLMONEY("SMALLMONEY", false, false, true, false, false, false, true, true),

    SQL_VARIANT("SQL_VARIANT", false, false, true, false, false, false, true, true),

    SYSNAME("SYSNAME", false, false, true, false, false, false, true, true),

    TEXT("TEXT", false, false, true, false, false, true, true, true),

    TIME("TIME", true, false, true, false, false, false, true, true),

    TIMESTAMP("TIMESTAMP", false, false, true, false, false, false, true, true),


    TINYINT("TINYINT", false, false, true, false, false, false, true, true),

    UNIQUEIDENTIFIER("UNIQUEIDENTIFIER", false, false, true, false, false, false, true, true),


    VARBINARY("VARBINARY", true, false, true, false, false, false, true, true),

    VARBINARY_MAX("VARBINARY(MAX)", false, false, true, false, false, false, true, true),

    VARCHAR("VARCHAR", true, false, true, false, false, true, true, true),

    VARCHAR_MAX("VARCHAR(MAX)", false, false, true, false, false, true, true, true),

    XML("XML", false, false, true, false, false, false, true, true),


    ;
    private ColumnType columnType;

    public static SqlServerColumnTypeEnum getByType(String dataType) {
        return COLUMN_TYPE_MAP.get(dataType.toUpperCase());
    }

    private static Map<String, SqlServerColumnTypeEnum> COLUMN_TYPE_MAP = Maps.newHashMap();

    static {
        for (SqlServerColumnTypeEnum value : SqlServerColumnTypeEnum.values()) {
            COLUMN_TYPE_MAP.put(value.getColumnType().getTypeName(), value);
        }
    }

    public ColumnType getColumnType() {
        return columnType;
    }


    SqlServerColumnTypeEnum(String dataTypeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue) {
        this.columnType = new ColumnType(dataTypeName, supportLength, supportScale, supportNullable, supportAutoIncrement, supportCharset, supportCollation, supportComments, supportDefaultValue, false, false, false);
    }

    @Override
    public String buildCreateColumnSql(TableColumn column) {
        SqlServerColumnTypeEnum type = this;
        StringBuilder script = new StringBuilder();

        script.append("[").append(column.getName()).append("]").append(" ");

        script.append(buildDataType(column, type)).append(" ");

        script.append(buildSparse(column, type)).append(" ");

        script.append(buildDefaultValue(column, type)).append(" ");

        script.append(buildNullable(column, type)).append(" ");

        script.append(buildCollation(column, type)).append(" ");

        return script.toString();
    }

    public String buildUpdateColumnSql(TableColumn column) {
        SqlServerColumnTypeEnum type = this;

        StringBuilder script = new StringBuilder();

        script.append("[").append(column.getName()).append("]").append(" ");

        script.append(buildDataType(column, type)).append(" ");

        script.append(buildNullable(column, type)).append(" \ngo\n");

        if (StringUtils.isNotBlank(column.getDefaultValue()) && column.getOldColumn().getDefaultValue() != null && !StringUtils.equalsIgnoreCase(column.getDefaultValue(), column.getOldColumn().getDefaultValue())) {
            script.append("ALTER TABLE ").append("[").append(column.getSchemaName()).append("].[").append(column.getTableName()).append("]");
            script.append(" ").append("DROP CONSTRAINT ").append("[").append(column.getDefaultConstraintName()).append("]");

            script.append("ALTER TABLE ").append("[").append(column.getSchemaName()).append("].[").append(column.getTableName()).append("]");
            script.append(" ").append("ADD ").append(buildDefaultValue(column, type)).append(" for ").append(column.getName()).append(" \ngo\n");
        }

        if (StringUtils.isNotBlank(column.getDefaultValue()) && column.getOldColumn().getDefaultValue() == null) {
            script.append("ALTER TABLE ").append("[").append(column.getSchemaName()).append("].[").append(column.getTableName()).append("]");
            script.append(" ").append("ADD ").append(buildDefaultValue(column, type)).append(" for ").append(column.getName()).append(" \ngo\n");
        }


        if (!Objects.equals(column.getSparse(), column.getOldColumn().getSparse())) {
            script.append("ALTER TABLE ").append("[").append(column.getSchemaName()).append("].[").append(column.getTableName()).append("]");
            script.append(" ").append("ALTER COLUMN ").append("[").append(column.getName()).append("]").append(" add ").append("SPARSE").append(" \ngo\n");
        }

        if (!Objects.equals(column.getCollationName(), column.getOldColumn().getCollationName())) {
            script.append("ALTER TABLE ").append("[").append(column.getSchemaName()).append("].[").append(column.getTableName()).append("]");
            script.append(" ").append("ALTER COLUMN ").append("[").append(column.getName()).append("]").append(" ").append("COLLATE ").append(column.getCollationName()).append(" \ngo\n");
        }
        return script.toString();
    }

    private String buildSparse(TableColumn column, SqlServerColumnTypeEnum type) {
        if (Boolean.TRUE.equals(column.getSparse())) {
            return "SPARSE";
        } else {
            return "";
        }
    }

    private String buildCollation(TableColumn column, SqlServerColumnTypeEnum type) {
        if (!type.getColumnType().isSupportCollation() || StringUtils.isEmpty(column.getCollationName())) {
            return "";
        }
        return StringUtils.join("COLLATE ", column.getCollationName());
    }


    private String buildNullable(TableColumn column, SqlServerColumnTypeEnum type) {
        if (!type.getColumnType().isSupportNullable()) {
            return "";
        }
        if (column.getNullable() != null && 1 == column.getNullable()) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }

    private String buildDefaultValue(TableColumn column, SqlServerColumnTypeEnum type) {
        if (!type.getColumnType().isSupportDefaultValue() || StringUtils.isEmpty(column.getDefaultValue())) {
            return "";
        }

        if ("EMPTY_STRING".equalsIgnoreCase(column.getDefaultValue().trim())) {
            return StringUtils.join("DEFAULT ''");
        }

        if ("NULL".equalsIgnoreCase(column.getDefaultValue().trim())) {
            return StringUtils.join("DEFAULT NULL");
        }

        return StringUtils.join("DEFAULT ", column.getDefaultValue());
    }

    private String buildDataType(TableColumn column, SqlServerColumnTypeEnum type) {
        String columnType = type.columnType.getTypeName();
        if (Arrays.asList(CHAR, NCHAR, NVARCHAR, VARBINARY, VARCHAR).contains(type)) {
            StringBuilder script = new StringBuilder();
            script.append(columnType);
            if (column.getColumnSize() != null) {
                script.append("(").append(column.getColumnSize()).append(")");
            }

            return script.toString();
        }

        if (Arrays.asList(DECIMAL, FLOAT, TIMESTAMP, TIME, DATETIME2, DATETIMEOFFSET, FLOAT, NUMERIC).contains(type)) {
            StringBuilder script = new StringBuilder();
            script.append(columnType);
            if (column.getColumnSize() != null && column.getDecimalDigits() == null) {
                script.append("(").append(column.getColumnSize()).append(")");
            } else if (column.getColumnSize() != null && column.getDecimalDigits() != null) {
                script.append("(").append(column.getColumnSize()).append(",").append(column.getDecimalDigits()).append(")");
            }
            return script.toString();
        }

        if (Arrays.asList().contains(type)) {
            StringBuilder script = new StringBuilder();
            if (column.getColumnSize() == null) {
                script.append(columnType);
            } else {
                String[] split = columnType.split("TIMESTAMP");
                script.append("TIMESTAMP").append("(").append(column.getColumnSize()).append(")").append(split[1]);
            }
            return script.toString();
        }


        return columnType;
    }

    private static String RENAME_COLUMN_SCRIPT = "exec sp_rename '%s.%s','%s','COLUMN' \ngo";

    private String renameColumn(TableColumn tableColumn) {
        return String.format(RENAME_COLUMN_SCRIPT, tableColumn.getTableName(), tableColumn.getOldName(), tableColumn.getName());
    }


    @Override
    public String buildModifyColumn(TableColumn tableColumn) {

        if (EditStatus.DELETE.name().equals(tableColumn.getEditStatus())) {
            StringBuilder script = new StringBuilder();
            if (StringUtils.isNotBlank(tableColumn.getDefaultConstraintName())) {
                script.append("ALTER TABLE ").append("[").append(tableColumn.getSchemaName()).append("].[").append(tableColumn.getTableName()).append("]");
                script.append(" ").append("DROP CONSTRAINT ").append("[").append(tableColumn.getDefaultConstraintName()).append("]");
                script.append("\ngo\n");
            }
            script.append("ALTER TABLE ").append("[").append(tableColumn.getSchemaName()).append("].[").append(tableColumn.getTableName()).append("]");
            script.append(" ").append("DROP COLUMN ").append("[").append(tableColumn.getName()).append("]");
            script.append("\ngo\n");
            return script.toString();
        }
        if (EditStatus.ADD.name().equals(tableColumn.getEditStatus())) {
            StringBuilder script = new StringBuilder();
            script.append("ALTER TABLE ").append("[").append(tableColumn.getSchemaName()).append("].[").append(tableColumn.getTableName()).append("]");
            script.append(" ").append("ADD ").append(buildCreateColumnSql(tableColumn)).append(" \ngo\n");


            if (StringUtils.isNotBlank(tableColumn.getComment())) {
                script.append("\n").append(buildModifyColumnComment(tableColumn));
            }
            return script.toString();
        }
        if (EditStatus.MODIFY.name().equals(tableColumn.getEditStatus())) {
            StringBuilder script = new StringBuilder();

            if (!StringUtils.equalsIgnoreCase(tableColumn.getOldName(), tableColumn.getName())) {
                script.append(renameColumn(tableColumn));
                script.append("\n");
            }
            script.append("ALTER TABLE ").append("[").append(tableColumn.getSchemaName()).append("].[").append(tableColumn.getTableName()).append("]");
            script.append(" ").append("ALTER COLUMN ").append(buildUpdateColumnSql(tableColumn)).append(" \n");

            if (!Objects.equals(tableColumn.getComment(), tableColumn.getOldColumn().getComment())) {
                script.append("\n").append(buildModifyColumnComment(tableColumn));
            }

            return script.toString();

        }
        return "";
    }

    private static String COLUMN_MODIFY_COMMENT_SCRIPT = "IF ((SELECT COUNT(*) FROM ::fn_listextendedproperty('MS_Description',\n" +
            "'SCHEMA', N'%s',\n" +
            "'TABLE', N'%s',\n" +
            "'COLUMN', N'%s')) > 0)\n" +
            "  EXEC sp_updateextendedproperty\n" +
            "'MS_Description', N'%s',\n" +
            "'SCHEMA', N'%s',\n" +
            "'TABLE', N'%s',\n" +
            "'COLUMN', N'%s'\n" +
            "ELSE\n" +
            "  EXEC sp_addextendedproperty\n" +
            "'MS_Description', N'%s',\n" +
            "'SCHEMA', N'%s',\n" +
            "'TABLE', N'%s',\n" +
            "'COLUMN', N'%s'\n go";

    private String buildModifyColumnComment(TableColumn tableColumn) {
        return String.format(COLUMN_MODIFY_COMMENT_SCRIPT, tableColumn.getSchemaName(), tableColumn.getTableName(),
                tableColumn.getName(), tableColumn.getComment(), tableColumn.getSchemaName(), tableColumn.getTableName(), tableColumn.getName(),
                tableColumn.getComment(), tableColumn.getSchemaName(), tableColumn.getTableName(), tableColumn.getName());
    }

    public static List<ColumnType> getTypes() {
        return Arrays.stream(SqlServerColumnTypeEnum.values()).map(columnTypeEnum ->
                columnTypeEnum.getColumnType()
        ).toList();
    }
}
