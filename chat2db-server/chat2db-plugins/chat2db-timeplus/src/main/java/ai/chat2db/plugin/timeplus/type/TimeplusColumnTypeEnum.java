package ai.chat2db.plugin.timeplus.type;

import ai.chat2db.spi.ColumnBuilder;
import ai.chat2db.spi.enums.EditStatus;
import ai.chat2db.spi.model.ColumnType;
import ai.chat2db.spi.model.TableColumn;
import ai.chat2db.spi.util.SqlUtils;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public enum TimeplusColumnTypeEnum implements ColumnBuilder {
    String(
        "string",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Int8(
        "int8",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Int16(
        "int16",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Int32(
        "int32",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Int64(
        "int64",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Int128(
        "int128",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Int256(
        "int256",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UInt8(
        "uint8",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UInt16(
        "uint16",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UInt32(
        "uint32",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UInt64(
        "uint64",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UInt128(
        "uint128",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UInt256(
        "uint256",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Float32(
        "float32",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Float64(
        "float64",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Decimal(
        "decimal",
        true,
        true,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Boolean(
        "bool",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    FixedString(
        "fixed_string",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    UUID(
        "uuid",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Date(
        "date",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    DATE32(
        "date32",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    DateTime(
        "datetime",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    DateTime64(
        "datetime64",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Enum8(
        "enum8",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Enum16(
        "enum16",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Array(
        "array",
        false,
        false,
        false,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    JSON(
        "json",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Nested(
        "nested",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Map("map", true, true, true, false, false, false, true, true, false, false),
    IPv4(
        "ipv4",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    IPv6(
        "ipv6",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Point(
        "point",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Ring(
        "ring",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    Polygon(
        "polygon",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    MultiPolygon(
        "multi_polygon",
        false,
        false,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    AggregateFunction(
        "aggregate_function",
        true,
        true,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    ),
    SimpleAggregateFunction(
        "simple_aggregate_function",
        true,
        true,
        true,
        false,
        false,
        false,
        true,
        true,
        false,
        false
    );

    private static Map<String, TimeplusColumnTypeEnum> COLUMN_TYPE_MAP =
        Maps.newHashMap();

    static {
        for (TimeplusColumnTypeEnum value : TimeplusColumnTypeEnum.values()) {
            COLUMN_TYPE_MAP.put(value.getColumnType().getTypeName(), value);
        }
    }

    private ColumnType columnType;

    TimeplusColumnTypeEnum(
        String dataTypeName,
        boolean supportLength,
        boolean supportScale,
        boolean supportNullable,
        boolean supportAutoIncrement,
        boolean supportCharset,
        boolean supportCollation,
        boolean supportComments,
        boolean supportDefaultValue,
        boolean supportExtent,
        boolean supportValue
    ) {
        this.columnType = new ColumnType(
            dataTypeName,
            supportLength,
            supportScale,
            supportNullable,
            supportAutoIncrement,
            supportCharset,
            supportCollation,
            supportComments,
            supportDefaultValue,
            supportExtent,
            supportValue,
            false
        );
    }

    public static TimeplusColumnTypeEnum getByType(String dataType) {
        return COLUMN_TYPE_MAP.get(
            SqlUtils.removeDigits(dataType.toUpperCase())
        );
    }

    public static List<ColumnType> getTypes() {
        return Arrays.stream(TimeplusColumnTypeEnum.values())
            .map(columnTypeEnum -> columnTypeEnum.getColumnType())
            .toList();
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    @Override
    public String buildCreateColumnSql(TableColumn column) {
        TimeplusColumnTypeEnum type = COLUMN_TYPE_MAP.get(
            column.getColumnType()
        );
        if (type == null) {
            return "";
        }
        StringBuilder script = new StringBuilder();

        script.append("`").append(column.getName()).append("`").append(" ");

        script.append(buildNullableAndDataType(column, type)).append(" ");

        script.append(buildDefaultValue(column, type)).append(" ");

        script.append(buildComment(column, type)).append(" ");

        return script.toString();
    }

    @Override
    public String buildModifyColumn(TableColumn tableColumn) {
        if (EditStatus.DELETE.name().equals(tableColumn.getEditStatus())) {
            return StringUtils.join(
                "DROP COLUMN `",
                tableColumn.getName() + "`"
            );
        }
        if (EditStatus.ADD.name().equals(tableColumn.getEditStatus())) {
            return StringUtils.join(
                "ADD COLUMN ",
                buildCreateColumnSql(tableColumn)
            );
        }
        if (EditStatus.MODIFY.name().equals(tableColumn.getEditStatus())) {
            String modifyColumn = "";
            if (
                !StringUtils.equalsIgnoreCase(
                    tableColumn.getOldName(),
                    tableColumn.getName()
                )
            ) {
                modifyColumn = StringUtils.join(
                    "RENAME COLUMN `",
                    tableColumn.getOldName(),
                    "` TO `",
                    tableColumn.getName(),
                    "`, ",
                    buildCreateColumnSql(tableColumn)
                );
            }
            return StringUtils.join(
                modifyColumn,
                "MODIFY COLUMN ",
                buildCreateColumnSql(tableColumn)
            );
        }
        return "";
    }

    private String buildComment(
        TableColumn column,
        TimeplusColumnTypeEnum type
    ) {
        if (
            !type.columnType.isSupportComments() ||
            StringUtils.isEmpty(column.getComment())
        ) {
            return "";
        }
        return StringUtils.join("COMMENT '", column.getComment(), "'");
    }

    private String buildDefaultValue(
        TableColumn column,
        TimeplusColumnTypeEnum type
    ) {
        if (
            !type.getColumnType().isSupportDefaultValue() ||
            StringUtils.isEmpty(column.getDefaultValue())
        ) {
            return "";
        }

        if ("EMPTY_STRING".equalsIgnoreCase(column.getDefaultValue().trim())) {
            return StringUtils.join("DEFAULT ''");
        }

        if ("NULL".equalsIgnoreCase(column.getDefaultValue().trim())) {
            return StringUtils.join("DEFAULT NULL");
        }

        if (Arrays.asList(Enum8, Enum16).contains(type)) {
            return StringUtils.join("DEFAULT '", column.getDefaultValue(), "'");
        }

        if (Arrays.asList(Date).contains(type)) {
            return StringUtils.join("DEFAULT '", column.getDefaultValue(), "'");
        }

        if (Arrays.asList(DateTime).contains(type)) {
            if (
                "CURRENT_TIMESTAMP".equalsIgnoreCase(
                        column.getDefaultValue().trim()
                    )
            ) {
                return StringUtils.join("DEFAULT ", column.getDefaultValue());
            }
            return StringUtils.join("DEFAULT '", column.getDefaultValue(), "'");
        }

        return StringUtils.join("DEFAULT ", column.getDefaultValue());
    }

    private String buildNullableAndDataType(
        TableColumn column,
        TimeplusColumnTypeEnum type
    ) {
        StringBuilder script = new StringBuilder();
        script.append(buildDataType(column, type));

        if (!type.getColumnType().isSupportNullable()) {
            return script.toString();
        }
        if (column.getNullable() != null && 1 == column.getNullable()) {
            return "Nullable(" + script.append(")").toString();
        } else {
            return script.toString();
        }
    }

    private String buildDataType(
        TableColumn column,
        TimeplusColumnTypeEnum type
    ) {
        String columnType = type.columnType.getTypeName();
        if (Arrays.asList(FixedString).contains(type)) {
            return StringUtils.join(
                columnType,
                "(",
                column.getColumnSize(),
                ")"
            );
        }

        if (Arrays.asList(Decimal).contains(type)) {
            if (
                column.getColumnSize() == null ||
                column.getDecimalDigits() == null
            ) {
                return columnType;
            }
            if (
                column.getColumnSize() != null &&
                column.getDecimalDigits() == null
            ) {
                return StringUtils.join(
                    columnType,
                    "(",
                    column.getColumnSize() + ")"
                );
            }
            if (
                column.getColumnSize() != null &&
                column.getDecimalDigits() != null
            ) {
                return StringUtils.join(
                    columnType,
                    "(",
                    column.getColumnSize() +
                    "," +
                    column.getDecimalDigits() +
                    ")"
                );
            }
        }

        return columnType;
    }

    public String buildColumn(TableColumn column) {
        TimeplusColumnTypeEnum type = COLUMN_TYPE_MAP.get(
            column.getColumnType()
        );
        if (type == null) {
            return "";
        }
        StringBuilder script = new StringBuilder();

        script.append("`").append(column.getName()).append("`").append(" ");
        script.append(buildDataType(column, type)).append(" ");
        if (StringUtils.isNoneBlank(column.getComment())) {
            script
                .append("COMMENT")
                .append(" ")
                .append("'")
                .append(column.getComment())
                .append("'")
                .append(" ");
        }
        return script.toString();
    }

    private String unsignedDataType(String dataTypeName, String middle) {
        String[] split = dataTypeName.split(" ");
        if (split.length == 2) {
            return StringUtils.join(split[0], middle, split[1]);
        }
        return StringUtils.join(dataTypeName, middle);
    }
}
