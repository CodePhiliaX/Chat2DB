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


    private static final Map<String, DefaultValueProcessor> PROCESSOR_MAP = Map.of(
            MysqlColumnTypeEnum.BIT.name(), new MysqlBitProcessor(),
            MysqlColumnTypeEnum.YEAR.name(), new MysqlYearProcessor(),
            MysqlColumnTypeEnum.DECIMAL.name(), new MysqlDecimalProcessor(),
            MysqlColumnTypeEnum.TIMESTAMP.name(), new MysqlTimestampProcessor(),
            MysqlColumnTypeEnum.DATETIME.name(), new MysqlDateTimeProcessor()
    );
    public static final Set<String> FUNCTION_SET = Set.of("now()");

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        if (FUNCTION_SET.contains(dataValue.getValue())) {
            return dataValue.getValue();
        }
        String dataType = dataValue.getDateTypeName();
        if (GEOMETRY_TYPE.contains(dataType.toUpperCase())) {
            return new MysqlGeometryProcessor().convertSQLValueByType(dataValue);
        }
        return PROCESSOR_MAP.getOrDefault(dataType, new DefaultValueProcessor()).convertSQLValueByType(dataValue);
    }

    @Override
    public Object convertJDBCValueByType(JDBCDataValue dataValue) {

        String dataType = dataValue.getType();
        if (GEOMETRY_TYPE.contains(dataType.toUpperCase())) {
            return new MysqlGeometryProcessor().convertJDBCValueByType(dataValue);
        }
        return PROCESSOR_MAP.getOrDefault(dataType, new DefaultValueProcessor()).convertJDBCValueByType(dataValue);
    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        String dataType = dataValue.getType();
        if (GEOMETRY_TYPE.contains(dataType.toUpperCase())) {
            return new MysqlGeometryProcessor().convertJDBCValueStrByType(dataValue);
        }
        return PROCESSOR_MAP.getOrDefault(dataType, new DefaultValueProcessor()).convertJDBCValueStrByType(dataValue);
    }
}
