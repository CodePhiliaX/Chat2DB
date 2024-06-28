package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.plugin.oracle.value.template.OracleDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

/**
 * 功能描述
 *
 * @author: zgq
 * @date: 2024年06月05日 18:58
 */
public class OracleIntervalYMProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue(), dataValue.getPrecision());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return super.convertJDBCValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(convertJDBCValueByType(dataValue), dataValue.getPrecision());
    }

    public String wrap(String value, int precision) {
        return String.format(OracleDmlValueTemplate.INTEGER_YEAR_TO_MONTH_TEMPLATE, value, precision);
    }
}
