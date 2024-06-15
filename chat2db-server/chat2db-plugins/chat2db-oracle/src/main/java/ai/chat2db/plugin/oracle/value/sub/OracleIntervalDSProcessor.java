package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月05日 18:56
 */
public class OracleIntervalDSProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue(), dataValue.getPrecision(), dataValue.getScale());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return super.convertJDBCValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(convertJDBCValueByType(dataValue), dataValue.getPrecision(), dataValue.getScale());
    }

    private String wrap(String value, int precision, int scale) {
        return String.format(OracleDmlValueTemplate.INTEGER_DAY_TO_SECOND_TEMPLATE, value, precision, scale);
    }
}
