package ai.chat2db.plugin.mysql.value;

import ai.chat2db.plugin.mysql.type.MysqlColumnTypeEnum;
import ai.chat2db.plugin.mysql.type.MysqlValueProcessorEnum;
import ai.chat2db.spi.jdbc.DefaultSQLValueProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author: zgq
 * @date: 2024年05月24日 21:02
 */
public class MysqlValueProcessor extends DefaultSQLValueProcessor {
    /**
     * @param rs
     * @param index
     * @return
     * @throws SQLException
     */
    @Override
    public String getSqlValueString(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        if (obj == null) {
            return null;
        }
        String columnTypeName = rs.getMetaData().getColumnTypeName(index);
        if (MysqlColumnTypeEnum.GEOMETRY.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.POINT.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.LINESTRING.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.POLYGON.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.MULTIPOINT.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.MULTILINESTRING.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.MULTIPOLYGON.name().equalsIgnoreCase(columnTypeName)
                || MysqlColumnTypeEnum.GEOMETRYCOLLECTION.name().equalsIgnoreCase(columnTypeName)
        ) {
            return MysqlValueProcessorEnum.GEOMETRY.getSqlValueString(rs, index);
        } else {
            super.getSqlValueString(rs, index);
        }
        return null;
    }

}
