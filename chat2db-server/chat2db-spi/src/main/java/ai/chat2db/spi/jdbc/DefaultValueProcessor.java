package ai.chat2db.spi.jdbc;

import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;

/**
 * @author: zgq
 * @date: 2024年05月24日 14:30
 */
public class DefaultValueProcessor extends BaseValueProcessor {

    public static final String[] DATE_FORMATS = {
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss",
            "HH:mm:ss"
    };

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        String value = dataValue.getValue();
        return getString(value);
    }


    @Override
    public Object convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        String value = dataValue.getString();
        return getString(value);

    }

    private boolean isNumber(String value) {
        return NumberUtils.isCreatable(value);
    }

    private String getString(String value) {
        if (isNumber(value)) {
            return value;
        }
        try {
            DateUtils.parseDate(value, DATE_FORMATS);
            return StringUtils.wrap(value, "'");
        } catch (ParseException e) {
            return StringUtils.wrap(EasyStringUtils.escapeString(value), "'");
        }
    }
}
