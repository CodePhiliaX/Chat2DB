package ai.chat2db.plugin.sundb.type;

import ai.chat2db.spi.ColumnBuilder;
import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.ColumnType;
import ai.chat2db.spi.model.TableColumn;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum SUNDBColumnTypeEnum implements ColumnBuilder {

    BIGINT("BIGINT", false, false, true, false, false, false, true, true, false, false),

    BINARY("BINARY", false, false, true, false, false, false, true, true, false, false),

    BINARY_VARYING("BINARY VARYING", false, false, true, false, false, false, true, true, false, false),

    BINARY_LONG_VARYING("BINARY LONG VARYING", false, false, true, false, false, false, true, true, false, false),

    BOOLEAN("BOOLEAN", false, false, true, true, false, false, true, true, false, false),

    CHAR("CHAR", true, false, true, false, false, false, true, true, false, true),

    CHARACTER("CHARACTER", true, false, true, false, false, false, true, true, false, true),

    CHARACTER_VARYING("CHARACTER VARYING", true, false, true, false, false, false, true, true, false, true),

    CHARACTER_LONG_VARYING("CHARACTER LONG VARYING", true, false, true, false, false, false, true, true, false, true),

    DATE("DATE", false, false, true, false, false, false, true, true, false, false),

    DEC("DEC", true, true, true, false, false, false, true, true, false, false),

    DECIMAL("DECIMAL", true, true, true, false, false, false, true, true, false, false),

    DOUBLE("DOUBLE", false, false, true, false, false, false, true, true, false, false),

    DOUBLE_PRECISION("DOUBLE PRECISION", false, false, true, false, false, false, true, true, false, false),

    FLOAT("FLOAT", true, false, true, false, false, false, true, true, false, false),
    FLOAT4("FLOAT4", true, false, true, false, false, false, true, true, false, false),
    FLOAT8("FLOAT8", true, false, true, false, false, false, true, true, false, false),

    INT("INT", false, false, true, true, false, false, true, true, false, false),
    INT2("INT2", false, false, true, true, false, false, true, true, false, false),

    INT4("INT4", false, false, true, true, false, false, true, true, false, false),

    INT8("INT8", false, false, true, true, false, false, true, true, false, false),

    INTEGER("INTEGER", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_DAY("INTERVAL DAY", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_DAY_TO_HOUR("INTERVAL DAY TO HOUR", true, false, true, false, false, false, true, true, false, false),


    INTERVAL_DAY_TO_MINUTE("INTERVAL DAY TO MINUTE", true, false, true, false, false, false, true, true, false, false),

    INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", true, false, true, false, false, false, true, true, false, false),

    INTERVAL_HOUR("INTERVAL HOUR", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_HOUR_TO_MINUTE("INTERVAL HOUR TO MINUTE", true, false, true, false, false, false, true, true, false, false),

    INTERVAL_HOUR_TO_SECOND("INTERVAL HOUR TO SECOND", true, false, true, false, false, false, true, true, false, false),

    INTERVAL_MINUTE("INTERVAL MINUTE", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_MINUTE_TO_SECOND("INTERVAL MINUTE TO SECOND", true, false, true, false, false, false, true, true, false, false),

    INTERVAL_MONTH("INTERVAL MONTH", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_SECOND("INTERVAL SECOND", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_YEAR("INTERVAL YEAR", false, false, true, false, false, false, true, true, false, false),

    INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", true, false, true, false, false, false, true, true, false, false),

    LONG_BINARY_VARYING("LONG BINARY VARYING", false, false, true, false, false, false, true, true, false, false),

    LONG_CHAR_VARYING("LONG CHAR VARYING", true, false, true, false, false, false, true, true, false, true),

    LONG_CHARACTER_VARYING("LONG CHARACTER VARYING", true, false, true, false, false, false, true, true, false, true),

    LONG_VARCHAR("LONG VARCHAR", true, false, true, false, false, false, true, true, false, true),

    NATIVE_BIGINT("NATIVE_BIGINT", false, false, true, false, false, false, true, true, false, false),

    NATIVE_DOUBLE("NATIVE_DOUBLE", false, false, true, false, false, false, true, true, false, false),

    NATIVE_INTEGER("NATIVE_INTEGER", false, false, true, false, false, false, true, true, false, false),

    NATIVE_REAL("NATIVE_REAL", false, false, true, false, false, false, true, true, false, false),

    NATIVE_SMALLINT("NATIVE_SMALLINT", false, false, true, false, false, false, true, true, false, false),

    NUMBER("NUMBER", true, true, true, false, false, false, true, true, false, false),

    NUMBERIC("NUMBERIC", true, true, true, false, false, false, true, true, false, false),

    ROWID("ROWID", true, true, true, false, false, false, true, true, false, false),

    SMALLINT("SMALLINT", false, false, true, false, false, false, true, true, false, false),

    TIME("TIME", false, false, true, false, false, false, true, true, false, false),

    TIMESTAMP("TIMESTAMP", false, false, true, false, false, false, true, true, false, false),

    VARBINARY("VARBINARY", false, false, true, false, false, false, true, true, false, false),

    VARCHAR("VARCHAR", true, false, true, false, false, false, true, true, false, true),

    VARCHAR2("VARCHAR2", true, false, true, false, false, false, true, true, false, true),

    ;
    private ColumnType columnType;

    public static SUNDBColumnTypeEnum getByType(String dataType) {
        return COLUMN_TYPE_MAP.get(dataType.toUpperCase());
    }

    private static Map<String, SUNDBColumnTypeEnum> COLUMN_TYPE_MAP = Maps.newHashMap();

    static {
        for (SUNDBColumnTypeEnum value : SUNDBColumnTypeEnum.values()) {
            COLUMN_TYPE_MAP.put(value.getColumnType().getTypeName(), value);
        }
    }

    public ColumnType getColumnType() {
        return columnType;
    }


    SUNDBColumnTypeEnum(String dataTypeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent, boolean supportUnit) {
        this.columnType = new ColumnType(dataTypeName, supportLength, supportScale, supportNullable, supportAutoIncrement, supportCharset, supportCollation, supportComments, supportDefaultValue, supportExtent, false, supportUnit);
    }

    @Override
    public String buildCreateColumnSql(TableColumn column) {
        SUNDBColumnTypeEnum type = COLUMN_TYPE_MAP.get(column.getColumnType().toUpperCase());
        if (type == null) {
            return "";
        }
        StringBuilder script = new StringBuilder();

        script.append("\"").append(column.getName()).append("\"").append(" ");

        script.append(buildDataType(column, type)).append(" ");

        script.append(buildDefaultValue(column, type)).append(" ");

        script.append(buildAutoIncrement(column,type)).append(" ");

        script.append(buildNullable(column, type)).append(" ");

        return script.toString();
    }

    private String buildAutoIncrement(TableColumn column, SUNDBColumnTypeEnum type) {
        if(!type.getColumnType().isSupportAutoIncrement()){
            return "";
        }
        if (column.getAutoIncrement() != null && column.getAutoIncrement()
                && column.getSeed() != null && column.getSeed() > 0 && column.getIncrement() != null && column.getIncrement() > 0) {
            return "IDENTITY(" + column.getSeed() + "," + column.getIncrement() + ")";
        }
        if (column.getAutoIncrement() != null && column.getAutoIncrement()) {
            return "IDENTITY(1,1)";
        }
        return "";
    }

    private String buildNullable(TableColumn column, SUNDBColumnTypeEnum type) {
        if (!type.getColumnType().isSupportNullable()) {
            return "";
        }
        if (column.getNullable() != null && 1 == column.getNullable()) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }

    private String buildDefaultValue(TableColumn column, SUNDBColumnTypeEnum type) {
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

    private String buildDataType(TableColumn column, SUNDBColumnTypeEnum type) {
        String columnType = type.columnType.getTypeName();
        if (Arrays.asList(CHAR, VARCHAR, VARCHAR2).contains(type)) {
            StringBuilder script = new StringBuilder();
            script.append(columnType);
            if (column.getColumnSize() != null && StringUtils.isEmpty(column.getUnit())) {
                script.append("(").append(column.getColumnSize()).append(")");
            } else if (column.getColumnSize() != null && !StringUtils.isEmpty(column.getUnit())) {
                script.append("(").append(column.getColumnSize()).append(" ").append(column.getUnit()).append(")");
            }
            return script.toString();
        }

        if (Arrays.asList(DECIMAL,DEC, FLOAT, NUMBER, TIMESTAMP, NUMBERIC).contains(type)) {
            StringBuilder script = new StringBuilder();
            script.append(columnType);
            if (column.getColumnSize() != null && column.getDecimalDigits() == null) {
                script.append("(").append(column.getColumnSize()).append(")");
            } else if (column.getColumnSize() != null && column.getDecimalDigits() != null) {
                script.append("(").append(column.getColumnSize()).append(",").append(column.getDecimalDigits()).append(")");
            }
            return script.toString();
        }

        if (Arrays.asList(INTERVAL_DAY_TO_HOUR,
                INTERVAL_DAY_TO_MINUTE, INTERVAL_DAY_TO_SECOND,
                INTERVAL_HOUR_TO_MINUTE,
                INTERVAL_HOUR_TO_SECOND,
                INTERVAL_MINUTE_TO_SECOND,
                INTERVAL_YEAR_TO_MONTH).contains(type)) {
            StringBuilder script = new StringBuilder();
            if (column.getColumnSize() == null) {
                script.append(columnType);
            } else {
                String[] split = columnType.split(" ");
                if (split.length == 4) {
                    script.append(split[0]).append(" ").append(split[1]).append(" (").append(column.getColumnSize()).append(") ").append(split[2]).append(" ").append(split[3]);
                }
            }
            return script.toString();
        }

        return columnType;
    }


    @Override
    public String buildModifyColumn(TableColumn tableColumn) {

        if (EditStatus.DELETE.name().equals(tableColumn.getEditStatus())) {
            StringBuilder script = new StringBuilder();
            script.append("ALTER TABLE ").append("\"").append(tableColumn.getSchemaName()).append("\".\"").append(tableColumn.getTableName()).append("\"");
            script.append(" ").append("DROP COLUMN ").append("\"").append(tableColumn.getName()).append("\"");
            return script.toString();
        }
        if (EditStatus.ADD.name().equals(tableColumn.getEditStatus())) {
            StringBuilder script = new StringBuilder();
            script.append("ALTER TABLE ").append("\"").append(tableColumn.getSchemaName()).append("\".\"").append(tableColumn.getTableName()).append("\"");
            script.append(" ").append("ADD (").append(buildCreateColumnSql(tableColumn)).append(")");
            return script.toString();
        }
        if (EditStatus.MODIFY.name().equals(tableColumn.getEditStatus())) {
            StringBuilder script = new StringBuilder();
            script.append("ALTER TABLE ").append("\"").append(tableColumn.getSchemaName()).append("\".\"").append(tableColumn.getTableName()).append("\"");
            script.append(" ").append("MODIFY (").append(buildCreateColumnSql(tableColumn)).append(") \n");

            if (!StringUtils.equalsIgnoreCase(tableColumn.getOldName(), tableColumn.getName())) {
                script.append(";");
                script.append("ALTER TABLE ").append("\"").append(tableColumn.getSchemaName()).append("\".\"").append(tableColumn.getTableName()).append("\"");
                script.append(" ").append("RENAME COLUMN ").append("\"").append(tableColumn.getOldName()).append("\"").append(" TO ").append("\"").append(tableColumn.getName()).append("\"");

            }
            return script.toString();

        }
        return "";
    }

    public static List<ColumnType> getTypes() {
        return Arrays.stream(SUNDBColumnTypeEnum.values()).map(columnTypeEnum ->
                columnTypeEnum.getColumnType()
        ).toList();
    }
}
