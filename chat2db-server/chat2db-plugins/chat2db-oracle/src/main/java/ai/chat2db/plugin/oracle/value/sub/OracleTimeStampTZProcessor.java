package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年06月05日 17:32
 */
public class OracleTimeStampTZProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue(), dataValue.getScale());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        // TODO: return:2024-06-05 17:32:52.849 +8:00 but it actually is 2024-06-05 17:32:52.849000 +8:00
        return super.convertJDBCValueByType(dataValue);

    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(convertJDBCValueByType(dataValue), dataValue.getScale());
    }

    private String wrap(String value, int scale) {
        if (scale == 0) {
            return String.format(OracleDmlValueTemplate.TIMESTAMP_TZ_WITHOUT_NANOS_TEMPLATE, value);
        }
        return String.format(OracleDmlValueTemplate.TIMESTAMP_TZ_TEMPLATE, value, scale);
    }
}
