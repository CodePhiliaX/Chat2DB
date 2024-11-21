package ai.chat2db.plugin.duckdb.type;

import ai.chat2db.spi.ColumnBuilder;
import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.ColumnType;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum DuckDBColumnTypeEnum implements ColumnBuilder {

    JSON("JSON", false, false, true, false, false, false, true, true, false, false),

    BIGINT("BIGINT", false, false, true, false, false, false, true, true, false, false),

    BINARY("BINARY", false, false, true, false, false, false, true, true, false, false),

    BIT("BIT", false, false, true, false, false, false, true, true, false, false),

    BLOB("BLOB", false, false, true, false, false, false, true, true, false, false),

    BOOL("BOOL", false, false, true, false, false, false, true, true, false, false),

    BOOLEAN("BOOLEAN", false, false, true, false, false, false, true, true, false, false),

    BPCHAR("BPCHAR", false, false, true, false, false, false, true, true, false, false),

    BYTEA("BYTEA", false, false, true, false, false, false, true, true, false, false),

    CHAR("CHAR", true, false, true, false, false, false, true, true, false, false),

    DATE("DATE", false, false, true, false, false, false, true, true, false, false),

    DATETIME("DATETIME", false, false, true, false, false, false, true, true, false, false),

    DEC("DEC", false, false, true, false, false, false, true, true, false, false),

    DECIMAL("DECIMAL", true, true, true, false, false, false, true, true, false, false),

    DOUBLE("DOUBLE", false, false, true, false, false, false, true, true, false, false),

    ENUM("ENUM", false, false, true, false, false, false, true, true, false, false),

    FLOAT("FLOAT", true, false, true, false, false, false, true, true, false, false),

    FLOAT4("FLOAT4", true, false, true, false, false, false, true, true, false, false),

    FLOAT8("FLOAT8", true, false, true, false, false, false, true, true, false, false),

    GUID("GUID", false, false, true, false, false, false, true, true, false, false),

    HUGEINT("HUGEINT", false, false, true, true, false, false, true, true, false, false),

    INT("INT", false, false, true, false, false, false, true, true, false, false),
    INT1("INT1", false, false, true, false, false, false, true, true, false, false),
    INT128("INT128", false, false, true, false, false, false, true, true, false, false),
    INT16("INT16", false, false, true, false, false, false, true, true, false, false),
    INT2("INT2", false, false, true, false, false, false, true, true, false, false),
    INT32("INT32", false, false, true, false, false, false, true, true, false, false),
    INT4("INT4", false, false, true, false, false, false, true, true, false, false),
    INT64("INT64", false, false, true, false, false, false, true, true, false, false),
    INT8("INT8", false, false, true, false, false, false, true, true, false, false),

    INTEGER("INTEGER", false, false, true, false, false, false, true, true, false, false),

    INTEGRAL("INTEGRAL", false, false, true, false, false, false, true, true, false, false),
    INTERVAL("INTERVAL", false, false, true, false, false, false, true, true, false, false),

    LIST("LIST", false, false, true, false, false, false, true, true, false, false),

    LOGICAL("LOGICAL", false, false, true, false, false, false, true, true, false, false),

    LONG("LONG", false, false, true, false, false, false, true, true, false, false),

    MAP("MAP", false, false, true, false, false, false, true, true, false, false),

    NULL("NULL", false, false, true, false, false, false, true, true, false, false),

    NUMERIC("NUMERIC", false, false, true, false, false, false, true, true, false, false),

    NVARCHAR("NVARCHAR", true, false, true, false, false, false, true, true, false, false),

    OID("OID", false, false, true, false, false, false, true, true, false, false),

    REAL("REAL", false, false, true, false, false, false, true, true, false, false),

    ROW("ROW", false, false, true, false, false, false, true, true, false, false),

    SHORT("SHORT", false, false, true, false, false, false, true, true, false, false),

    SIGNED("SIGNED", false, false, true, false, false, false, true, true, false, false),

    SMALLINT("SMALLINT", false, false, true, false, false, false, true, true, false, false),

    STRING("STRING", false, false, true, false, false, false, true, true, false, false),

    STRUCT("STRUCT", false, false, true, false, false, false, true, true, false, false),

    TEXT("TEXT", false, false, true, false, false, false, true, true, false, false),

    TIME("TIME", false, false, true, false, false, false, true, true, false, false),

    TIMESTAMP("TIMESTAMP", false, false, true, false, false, false, true, true, false, false),

    TIMSTAMP_MS("TIMSTAMP_MS", false, false, true, false, false, false, true, true, false, false),

    TIMSTAMP_NS("TIMSTAMP_NS", false, false, true, false, false, false, true, true, false, false),
    TIMSTAMP_S("TIMSTAMP_S", false, false, true, false, false, false, true, true, false, false),
    TIMSTAMP_US("TIMSTAMP_US", false, false, true, false, false, false, true, true, false, false),

    TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", false, false, true, false, false, false, true, true, false, false),

    TIME_WITH_TIME_ZONE("TIME WITH TIME ZONE", false, false, true, false, false, false, true, true, false, false),


    TINYINT("TINYINT", false, false, true, false, false, false, true, true, false, false),

    UBIGINT("UBIGINT", false, false, true, false, false, false, true, true, false, false),

    UHUGEINT("UHUGEINT", false, false, true, false, false, false, true, true, false, false),

    UINT128("UINT128", false, false, true, false, false, false, true, true, false, false),

    UINT16("UINT16", false, false, true, false, false, false, true, true, false, false),

    UINT32("UINT32", false, false, true, false, false, false, true, true, false, false),

    UINT64("UINT64", false, false, true, false, false, false, true, true, false, false),

    UINT8("UINT8", false, false, true, false, false, false, true, true, false, false),

    UINTEGER("UINTEGER", false, false, true, false, false, false, true, true, false, false),

    UNION("UNION", false, false, true, false, false, false, true, true, false, false),

    USMALLINT("USMALLINT", false, false, true, false, false, false, true, true, false, false),

    UTINYINT("UTINYINT", false, false, true, false, false, false, true, true, false, false),

    UUID("UUID", false, false, true, false, false, false, true, true, false, false),

    VARBINARY("VARBINARY", false, false, true, false, false, false, true, true, false, false),

    VARCHAR("VARCHAR", true, false, true, false, false, false, true, true, false, true),

    VARINT("VARINT", false, false, true, false, false, false, true, true, false, false),

    ARRAY("ARRAY", false, false, true, false, false, false, true, true, false, false),
    ;
    private ColumnType columnType;

    public static DuckDBColumnTypeEnum getByType(String dataType) {
        String type = SqlUtils.removeDigits(dataType.toUpperCase());
        return COLUMN_TYPE_MAP.get(type);
    }

    private static Map<String, DuckDBColumnTypeEnum> COLUMN_TYPE_MAP = Maps.newHashMap();

    static {
        for (DuckDBColumnTypeEnum value : DuckDBColumnTypeEnum.values()) {
            COLUMN_TYPE_MAP.put(value.getColumnType().getTypeName(), value);
        }
    }

    public ColumnType getColumnType() {
        return columnType;
    }


    DuckDBColumnTypeEnum(String dataTypeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent, boolean supportUnit) {
        this.columnType = new ColumnType(dataTypeName, supportLength, supportScale, supportNullable, supportAutoIncrement, supportCharset, supportCollation, supportComments, supportDefaultValue, supportExtent, false, supportUnit);
    }

    @Override
    public String buildCreateColumnSql(TableColumn column) {
        DuckDBColumnTypeEnum type = COLUMN_TYPE_MAP.get(column.getColumnType().toUpperCase());
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

    private String buildAutoIncrement(TableColumn column, DuckDBColumnTypeEnum type) {
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

    private String buildNullable(TableColumn column, DuckDBColumnTypeEnum type) {
        if (!type.getColumnType().isSupportNullable()) {
            return "";
        }
        if (column.getNullable() != null && 1 == column.getNullable()) {
            return "NULL";
        } else {
            return "NOT NULL";
        }
    }

    private String buildDefaultValue(TableColumn column, DuckDBColumnTypeEnum type) {
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

    private String buildDataType(TableColumn column, DuckDBColumnTypeEnum type) {
        String columnType = type.columnType.getTypeName();
        if (Arrays.asList(VARCHAR, STRING, BPCHAR, NVARCHAR, TEXT).contains(type)) {
            StringBuilder script = new StringBuilder();
            script.append(columnType);
            if (column.getColumnSize() != null && StringUtils.isEmpty(column.getUnit())) {
                script.append("(").append(column.getColumnSize()).append(")");
            } else if (column.getColumnSize() != null && !StringUtils.isEmpty(column.getUnit())) {
                script.append("(").append(column.getColumnSize()).append(" ").append(column.getUnit()).append(")");
            }
            return script.toString();
        }

        if (Arrays.asList(DECIMAL, FLOAT, TIMESTAMP).contains(type)) {
            StringBuilder script = new StringBuilder();
            script.append(columnType);
            if (column.getColumnSize() != null && column.getDecimalDigits() == null) {
                script.append("(").append(column.getColumnSize()).append(")");
            } else if (column.getColumnSize() != null && column.getDecimalDigits() != null) {
                script.append("(").append(column.getColumnSize()).append(",").append(column.getDecimalDigits()).append(")");
            }
            return script.toString();
        }

        if (Arrays.asList(TIME_WITH_TIME_ZONE, TIMSTAMP_US).contains(type)) {
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
        return Arrays.stream(DuckDBColumnTypeEnum.values()).map(columnTypeEnum ->
                columnTypeEnum.getColumnType()
        ).toList();
    }
}
