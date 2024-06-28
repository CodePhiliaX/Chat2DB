package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

import java.sql.Date;
import java.util.Calendar;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年06月01日 12:57
 */
public class MysqlYearProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return dataValue.getValue();
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        Date date = dataValue.getDate();
        if (!isValidYear(dataValue)) {
            return "0000";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        String yStr;
        String yZerosPadding = "0000";
        if (year < 1000) {
            yStr = "" + year;
            yStr = yZerosPadding.substring(0, (4 - yStr.length())) + yStr;
        } else {
            yStr = "" + year;
        }
        return yStr;
    }

    private boolean isValidYear(JDBCDataValue data) {
        byte[] buffer = data.getBytes();
        String stringValue = new String(buffer);
        return stringValue.length() <= 0
                || stringValue.charAt(0) != '0'
                || !"0000-00-00".equals(stringValue)
                && !"0000-00-00 00:00:00".equals(stringValue)
                && !"00000000000000".equals(stringValue)
                && !"0".equals(stringValue)
                && !"00000000".equals(stringValue)
                && !"0000".equals(stringValue);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return getJdbcValue(dataValue);
    }
}
