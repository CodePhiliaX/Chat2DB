package ai.chat2db.spi.jdbc;

import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.ValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author: zgq
 * @date: 2024年05月30日 15:33
 */
public abstract class BaseValueProcessor implements ValueProcessor {

    @Override
    public String getSqlValueString(SQLDataValue dataValue) {
        if (Objects.isNull(dataValue.getValue())) {
            return "NULL";
        }
        return convertSQLValueByType(dataValue);

    }


    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(dataValue.getObject())) {
            return null;
        }
        if (value instanceof String emptySry) {
            if (StringUtils.isBlank(emptySry)) {
                return emptySry;
            }
        }
        return convertJDBCValueByType(dataValue);
    }


    @Override
    public String getJdbcValueString(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            return "NULL";
        }
        if (value instanceof String stringValue) {
            if (StringUtils.isBlank(stringValue)) {
                return EasyStringUtils.quoteString(stringValue);
            }
        }
        return convertJDBCValueStrByType(dataValue);
    }

    public abstract String convertSQLValueByType(SQLDataValue dataValue);

    public abstract String convertJDBCValueByType(JDBCDataValue dataValue);

    public abstract String convertJDBCValueStrByType(JDBCDataValue dataValue);
}
