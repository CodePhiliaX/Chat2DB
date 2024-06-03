package ai.chat2db.plugin.mysql.value;

import ai.chat2db.plugin.mysql.type.MysqlColumnTypeEnum;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

import java.util.Map;
import java.util.Set;

/**
 * @author: zgq
 * @date: 2024年05月24日 21:02
 * <br>
 *  TODO:
 *      attribute: [zerofill] example tinyint[5] zerofill 34->00034
 */
public class MysqlValueProcessor extends DefaultValueProcessor {

    private static final Set<String> GEOMETRY_TYPE = Set.of(MysqlColumnTypeEnum.GEOMETRY.name()
            , MysqlColumnTypeEnum.POINT.name()
            , MysqlColumnTypeEnum.LINESTRING.name()
            , MysqlColumnTypeEnum.POLYGON.name()
            , MysqlColumnTypeEnum.MULTIPOINT.name()
            , MysqlColumnTypeEnum.MULTILINESTRING.name()
            , MysqlColumnTypeEnum.MULTIPOLYGON.name()
            , MysqlColumnTypeEnum.GEOMETRYCOLLECTION.name());

    public static final Set<String> BINARY_TYPE = Set.of(MysqlColumnTypeEnum.VARBINARY.name()
            , MysqlColumnTypeEnum.BLOB.name()
            , MysqlColumnTypeEnum.LONGBLOB.name()
            , MysqlColumnTypeEnum.TINYBLOB.name()
            , MysqlColumnTypeEnum.MEDIUMBLOB.name());

    public static final Set<String> DATE_TYPE = Set.of(MysqlColumnTypeEnum.TIMESTAMP.name(),
                                                       MysqlColumnTypeEnum.DATETIME.name());

    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP = Map.of(
            MysqlColumnTypeEnum.BIT.name(), new MysqlBitProcessor(),
            MysqlColumnTypeEnum.YEAR.name(), new MysqlYearProcessor(),
            MysqlColumnTypeEnum.DECIMAL.name(), new MysqlDecimalProcessor(),
            MysqlColumnTypeEnum.BINARY.name(), new MysqlBinaryProcessor()
    );
    public static final Set<String> FUNCTION_SET = Set.of("now()", "default");

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        if (FUNCTION_SET.contains(dataValue.getValue().toLowerCase())) {
            return dataValue.getValue();
        }
        String dataType = dataValue.getDateTypeName();
        if (GEOMETRY_TYPE.contains(dataType.toUpperCase())) {
            return new MysqlGeometryProcessor().convertSQLValueByType(dataValue);
        }
        if (BINARY_TYPE.contains(dataType)) {
            return new MysqlVarBinaryProcessor().convertSQLValueByType(dataValue);
        }
        if (DATE_TYPE.contains(dataType)) {
            return new MysqlTimestampProcessor().convertSQLValueByType(dataValue);
        }
        return PROCESSOR_MAP.getOrDefault(dataType, new DefaultValueProcessor()).convertSQLValueByType(dataValue);
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        String dataType = dataValue.getType();
        if (GEOMETRY_TYPE.contains(dataType.toUpperCase())) {
            return new MysqlGeometryProcessor().convertJDBCValueByType(dataValue);
        }
        if (BINARY_TYPE.contains(dataType)) {
            return new MysqlVarBinaryProcessor().convertJDBCValueByType(dataValue);
        }
        if (DATE_TYPE.contains(dataType)) {
            return new MysqlTimestampProcessor().convertJDBCValueByType(dataValue);
        }
        return PROCESSOR_MAP.getOrDefault(dataType, new DefaultValueProcessor()).convertJDBCValueByType(dataValue);
    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        String dataType = dataValue.getType();
        if (GEOMETRY_TYPE.contains(dataType.toUpperCase())) {
            return new MysqlGeometryProcessor().convertJDBCValueStrByType(dataValue);
        }
        if (BINARY_TYPE.contains(dataType)) {
            return new MysqlVarBinaryProcessor().convertJDBCValueStrByType(dataValue);
        }
        if (DATE_TYPE.contains(dataType)) {
            return new MysqlTimestampProcessor().convertJDBCValueStrByType(dataValue);
        }
        return PROCESSOR_MAP.getOrDefault(dataType, new DefaultValueProcessor()).convertJDBCValueStrByType(dataValue);
    }
}
