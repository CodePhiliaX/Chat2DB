package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: zgq
 * @date: 2024年06月04日 16:33
 */
public class OracleDateProcessor extends DefaultValueProcessor {

    /**
     * @param dataValue
     * @return
     */
    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue());
    }

    /**
     * @param dataValue
     * @return
     */
    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        Timestamp timestamp = dataValue.getTimestamp();
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        if (formattedDateTime.endsWith("00:00:00")) {
            formattedDateTime = formattedDateTime.substring(0, formattedDateTime.length() - 9);
        }
        return formattedDateTime;

    }

    /**
     * @param dataValue
     * @return
     */
    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(convertJDBCValueByType(dataValue));
    }

    private String wrap(String value) {
        return String.format(OracleDmlValueTemplate.DATE_TEMPLATE, value);
    }
}
