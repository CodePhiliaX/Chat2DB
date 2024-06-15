package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月01日 18:26
 */
public class MysqlTimestampProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return super.convertSQLValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return isValidTimestamp(dataValue) ? new String(dataValue.getBytes()) : "0000-00-00 00:00:00";
    }

    protected boolean isValidTimestamp(JDBCDataValue data) {
        byte[] buffer = data.getBytes();
        String stringValue = new String(buffer);
        return stringValue.length() <= 0
                || stringValue.charAt(0) != '0'
                || !"0000-00-00".equals(stringValue)
                && !"0000-00-00 00:00:00".equals(stringValue)
                && !"00000000000000".equals(stringValue)
                && !"0".equals(stringValue);
    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return String.format(MysqlDmlValueTemplate.COMMON_TEMPLATE, convertJDBCValueByType(dataValue));
    }
}
