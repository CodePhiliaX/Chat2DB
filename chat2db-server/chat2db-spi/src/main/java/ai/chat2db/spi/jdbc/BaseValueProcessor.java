package ai.chat2db.spi.jdbc;

import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.ValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
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
        if (Objects.isNull(value)) {
            // mysql -> [date]->0000:00:00
            if (Chat2DBContext.getDBConfig().getDbType().equalsIgnoreCase("mysql")) {
                String stringValue = dataValue.getStringValue();
                if (Objects.nonNull(stringValue)) {
                    return stringValue;
                }
            }
            return null;
        }
        if (value instanceof String emptyStr) {
            if (StringUtils.isBlank(emptyStr)) {
                return emptyStr;
            }
        }
        return convertJDBCValueByType(dataValue);
    }


    @Override
    public String getJdbcValueString(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            // mysql -> [date]->0000:00:00
            if (Chat2DBContext.getDBConfig().getDbType().equalsIgnoreCase("mysql")) {
                String stringValue = dataValue.getStringValue();
                if (Objects.nonNull(stringValue)) {
                    return EasyStringUtils.escapeAndQuoteString(stringValue);
                }
            }
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
