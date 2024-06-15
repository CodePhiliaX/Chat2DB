package ai.chat2db.spi.jdbc;

import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author: zgq
 * @date: 2024年05月24日 14:30
 */
public class DefaultValueProcessor extends BaseValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return getString(dataValue.getValue());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return getString(dataValue.getString());

    }

    private boolean isNumber(String value) {
        return NumberUtils.isCreatable(value);
    }

    private String getString(String value) {
        if (isNumber(value)) {
            return value;
        }
        return EasyStringUtils.escapeAndQuoteString(value);
    }
}
