package ai.chat2db.plugin.oracle.value;

import ai.chat2db.plugin.oracle.value.factory.OracleValueProcessorFactory;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月03日 22:30
 */
public class OracleValueProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return OracleValueProcessorFactory.getValueProcessor(dataValue.getDateTypeName()).convertSQLValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        String type = dataValue.getType();
        return OracleValueProcessorFactory.getValueProcessor(dataValue.getType()).convertJDBCValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return OracleValueProcessorFactory.getValueProcessor(dataValue.getType()).convertJDBCValueStrByType(dataValue);
    }
}
