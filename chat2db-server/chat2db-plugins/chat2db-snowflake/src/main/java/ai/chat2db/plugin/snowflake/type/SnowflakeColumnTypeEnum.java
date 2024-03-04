package ai.chat2db.plugin.snowflake.type;

import ai.chat2db.spi.ColumnBuilder;
import ai.chat2db.spi.model.ColumnType;
import ai.chat2db.spi.model.TableColumn;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public enum SnowflakeColumnTypeEnum implements ColumnBuilder {

    NUMBER("NUMBER", true, true, true, true, false, false, true, true, false, false),
    DECIMAL("DECIMAL", true, true, true, true, false, false, true, true, false, false),
    NUMERIC("NUMERIC", true, true, true, true, false, false, true, true, false, false),
    INT("INT", false, false, true, true, false, false, true, true, false, false),
    INTEGER("INTEGER", false, false, true, true, false, false, true, true, false, false),
    BIGINT("BIGINT", false, false, true, true, false, false, true, true, false, false),
    SMALLINT("SMALLINT", false, false, true, true, false, false, true, true, false, false),
    TINYINT("TINYINT", false, false, true, true, false, false, true, true, false, false),
    BYTEINT("BYTEINT", false, false, true, true, false, false, true, true, false, false),
    FLOAT("FLOAT", true, true, true, true, false, false, true, true, false, false),
    DOUBLE("DOUBLE", true, true, true, true, false, false, true, true, false, false),

    VARCHAR("VARCHAR", true, false, true, false, true, true, true, true, false, false),
    CHAR("CHAR", true, false, true, false, true, true, true, true, false, false),
    STRING("STRING", true, false, true, false, true, true, true, true, false, false),
    TEXT("TEXT", true, false, true, false, true, true, true, true, false, false),
    BINARY("BINARY", true, false, true, false, true, true, true, true, false, false),
    VARBINARY("VARBINARY", true, false, true, false, true, true, true, true, false, false),

    BOOLEAN("BOOLEAN", false, false, true, false, false, false, true, true, false, false),

    DATE("DATE", false, false, true, false, false, false, true, true, false, false),
    DATETIME("DATETIME", true, false, true, false, false, false, true, true, true, false),
    TIME("TIME", true, false, true, false, false, false, true, true, false, false),
    TIMESTAMP("TIMESTAMP", true, false, true, false, false, false, true, true, true, false),
    TIMESTAMP_LTZ("TIMESTAMPLTZ", true, false, true, false, false, false, true, true, true, false),
    TIMESTAMP_NTZ("TIMESTAMPNTZ", true, false, true, false, false, false, true, true, true, false),
    TIMESTAMP_TZ("TIMESTAMPTZ", true, false, true, false, false, false, true, true, true, false),
    VARIANT("VARIANT", true, false, true, false, false, false, true, true, false, false),
    OBJECT("OBJECT", true, false, true, false, false, false, true, true, false, false),
    ARRAY("ARRAY", true, false, true, false, false, false, true, true, false, false),
    GEOGRAPHY("GEOGRAPHY", true, false, true, false, false, false, true, true, false, false),

    ;

    private ColumnType columnType;

    public static SnowflakeColumnTypeEnum getByType(String dataType) {
        return COLUMN_TYPE_MAP.get(dataType.toUpperCase());
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    SnowflakeColumnTypeEnum(String dataTypeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent, boolean supportValue) {
        this.columnType = new ColumnType(dataTypeName, supportLength, supportScale, supportNullable, supportAutoIncrement, supportCharset, supportCollation, supportComments, supportDefaultValue, supportExtent,supportValue,false);
    }

    private static Map<String, SnowflakeColumnTypeEnum> COLUMN_TYPE_MAP = Maps.newHashMap();

    static {
        for (SnowflakeColumnTypeEnum value : SnowflakeColumnTypeEnum.values()) {
            COLUMN_TYPE_MAP.put(value.getColumnType().getTypeName(), value);
        }
    }
    @Override
    public String buildCreateColumnSql(TableColumn column) {
        return null;
    }

    @Override
    public String buildModifyColumn(TableColumn tableColumn) {
        return null;
    }

    public static List<ColumnType> getTypes(){
        return Arrays.stream(SnowflakeColumnTypeEnum.values()).map(columnTypeEnum ->
                columnTypeEnum.getColumnType()
        ).toList();
    }


//
//
//    @Override
//    public String buildCreateColumnSql(TableColumn column) {
//        OceanBaseColumnTypeEnum type = COLUMN_TYPE_MAP.get(column.getColumnType().toUpperCase());
//        if (type == null) {
//            return "";
//        }
//        StringBuilder script = new StringBuilder();
//
//        script.append("`").append(column.getName()).append("`").append(" ");
//
//        script.append(buildDataType(column, type)).append(" ");
//
//        script.append(buildCharset(column,type)).append(" ");
//
//        script.append(buildCollation(column,type)).append(" ");
//
//        script.append(buildNullable(column,type)).append(" ");
//
//        script.append(buildDefaultValue(column,type)).append(" ");
//
//        script.append(buildExt(column,type)).append(" ");
//
//        script.append(buildAutoIncrement(column,type)).append(" ");
//
//        script.append(buildComment(column,type)).append(" ");
//
//        return script.toString();
//    }
//
//    private String buildCharset(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.getColumnType().isSupportCharset() || StringUtils.isEmpty(column.getCharSetName())){
//            return "";
//        }
//        return StringUtils.join("CHARACTER SET ",column.getCharSetName());
//    }
//
//    private String buildCollation(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.getColumnType().isSupportCollation() || StringUtils.isEmpty(column.getCollationName())){
//            return "";
//        }
//        return StringUtils.join("COLLATE ",column.getCollationName());
//    }
//
//    @Override
//    public String buildModifyColumn(TableColumn tableColumn) {
//
//        if (EditStatus.DELETE.name().equals(tableColumn.getEditStatus())) {
//            return StringUtils.join("DROP COLUMN `", tableColumn.getName() + "`");
//        }
//        if (EditStatus.ADD.name().equals(tableColumn.getEditStatus())) {
//            return StringUtils.join("ADD COLUMN ", buildCreateColumnSql(tableColumn));
//        }
//        if (EditStatus.MODIFY.name().equals(tableColumn.getEditStatus())) {
//            if (!StringUtils.equalsIgnoreCase(tableColumn.getOldName(), tableColumn.getName())) {
//                return StringUtils.join("CHANGE COLUMN `", tableColumn.getOldName(), "` ", buildCreateColumnSql(tableColumn));
//            } else {
//                return StringUtils.join("MODIFY COLUMN ", buildCreateColumnSql(tableColumn));
//            }
//        }
//        return "";
//    }
//
//    private String buildAutoIncrement(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.getColumnType().isSupportAutoIncrement()){
//            return "";
//        }
//        if (column.getAutoIncrement() != null && column.getAutoIncrement()) {
//            return "AUTO_INCREMENT";
//        }
//        return "";
//    }
//
//    private String buildComment(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.columnType.isSupportComments() || StringUtils.isEmpty(column.getComment())){
//            return "";
//        }
//        return StringUtils.join("COMMENT '",column.getComment(),"'");
//    }
//
//    private String buildExt(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.columnType.isSupportExtent() || StringUtils.isEmpty(column.getExtent())){
//            return "";
//        }
//        return column.getComment();
//    }
//
//    private String buildDefaultValue(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.getColumnType().isSupportDefaultValue() || StringUtils.isEmpty(column.getDefaultValue())){
//            return "";
//        }
//
//        if("EMPTY_STRING".equalsIgnoreCase(column.getDefaultValue().trim())){
//            return StringUtils.join("DEFAULT ''");
//        }
//
//        if("NULL".equalsIgnoreCase(column.getDefaultValue().trim())){
//            return StringUtils.join("DEFAULT NULL");
//        }
//
//        if(Arrays.asList(CHAR,VARCHAR,BINARY,VARBINARY, SET,ENUM).contains(type)){
//            return StringUtils.join("DEFAULT '",column.getDefaultValue(),"'");
//        }
//
//        if(Arrays.asList(DATE,TIME,YEAR).contains(type)){
//            return StringUtils.join("DEFAULT '",column.getDefaultValue(),"'");
//        }
//
//        if(Arrays.asList(DATETIME,TIMESTAMP).contains(type)){
//            if("CURRENT_TIMESTAMP".equalsIgnoreCase(column.getDefaultValue().trim())){
//                return StringUtils.join("DEFAULT ",column.getDefaultValue());
//            }
//            return StringUtils.join("DEFAULT '",column.getDefaultValue(),"'");
//        }
//
//        return StringUtils.join("DEFAULT ",column.getDefaultValue());
//    }
//
//    private String buildNullable(TableColumn column, OceanBaseColumnTypeEnum type) {
//        if(!type.getColumnType().isSupportNullable()){
//            return "";
//        }
//        if (column.getNullable()!=null && 1==column.getNullable()) {
//            return "NULL";
//        } else {
//            return "NOT NULL";
//        }
//    }
//
//    private String buildDataType(TableColumn column, OceanBaseColumnTypeEnum type) {
//        String columnType = type.columnType.getTypeName();
//        if (Arrays.asList(BINARY, VARBINARY, VARCHAR, CHAR).contains(type)) {
//            return StringUtils.join(columnType, "(", column.getColumnSize(), ")");
//        }
//
//        if (BIT.equals(type)) {
//            return StringUtils.join(columnType, "(", column.getColumnSize(), ")");
//        }
//
//        if (Arrays.asList(TIME, DATETIME, TIMESTAMP).contains(type)) {
//            if (column.getColumnSize() == null || column.getColumnSize() == 0) {
//                return columnType;
//            } else {
//                return StringUtils.join(columnType, "(", column.getColumnSize(), ")");
//            }
//        }
//
//
//        if (Arrays.asList(DECIMAL, FLOAT, DOUBLE).contains(type)) {
//            if (column.getColumnSize() == null || column.getDecimalDigits() == null) {
//                return columnType;
//            }
//            if (column.getColumnSize() != null && column.getDecimalDigits() == null) {
//                return StringUtils.join(columnType, "(", column.getColumnSize() + ")");
//            }
//            if (column.getColumnSize() != null && column.getDecimalDigits() != null) {
//                return StringUtils.join(columnType, "(", column.getColumnSize() + "," + column.getDecimalDigits() + ")");
//            }
//        }
//
//        if (Arrays.asList(DECIMAL_UNSIGNED, FLOAT_UNSIGNED, DECIMAL_UNSIGNED).contains(type)) {
//            if (column.getColumnSize() == null || column.getDecimalDigits() == null) {
//                return columnType;
//            }
//            if (column.getColumnSize() != null && column.getDecimalDigits() == null) {
//                return unsignedDataType(columnType, "(" + column.getColumnSize() + ")");
//            }
//            if (column.getColumnSize() != null && column.getDecimalDigits() != null) {
//                return unsignedDataType(columnType, "(" + column.getColumnSize() + "," + column.getDecimalDigits() + ")");
//            }
//        }
//
//        if(Arrays.asList(SET,ENUM).contains(type)){
//            if(!StringUtils.isEmpty( column.getValue())){
//                return StringUtils.join(columnType,"(",column.getValue(),")");
//            }
//            //List<String> enumList = column.
//        }
//
//        return columnType;
//    }
//
//    private String unsignedDataType(String dataTypeName, String middle) {
//        String[] split = dataTypeName.split(" ");
//        if (split.length == 2) {
//            return StringUtils.join(split[0], middle, split[1]);
//        }
//        return StringUtils.join(dataTypeName, middle);
//    }
//
//    public static List<ColumnType> getTypes(){
//       return Arrays.stream(OceanBaseColumnTypeEnum.values()).map(columnTypeEnum ->
//                columnTypeEnum.getColumnType()
//        ).toList();
//    }
//
//
}
