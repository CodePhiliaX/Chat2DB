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
        return dataValue.getStringValue();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(dataValue.getStringValue(), dataValue.getScale());
    }

    private String wrap(String value, int scale) {
        if (scale == 0) {
            return OracleDmlValueTemplate.wrapTimestampTzWithOutNanos(value);
        }
        return OracleDmlValueTemplate.wrapTimestampTz(value, scale);
    }
}
