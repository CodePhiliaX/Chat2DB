package ai.chat2db.plugin.oracle.value;

import ai.chat2db.plugin.oracle.type.OracleColumnTypeEnum;
import ai.chat2db.plugin.oracle.value.factory.OracleValueProcessorFactory;
import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author: zgq
 * @date: 2024年06月03日 22:30
 */
public class OracleValueProcessor extends DefaultValueProcessor {


    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        if (OracleColumnTypeEnum.LONG_RAW.getColumnType().getTypeName().equalsIgnoreCase(dataValue.getType())) {
            return convertJDBCValueByType(dataValue);
        }
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String emptyStr) {
            if (StringUtils.isBlank(emptyStr)) {
                return emptyStr;
            }
        }
        return convertJDBCValueByType(dataValue);
    }


    @Override
    public String getJdbcSqlValueString(JDBCDataValue dataValue) {
        if (OracleColumnTypeEnum.LONG_RAW.getColumnType().getTypeName().equalsIgnoreCase(dataValue.getType())) {
            return convertJDBCValueStrByType(dataValue);
        }
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            return "NULL";
        }
        if (value instanceof String stringValue) {
            if (StringUtils.isBlank(stringValue)) {
                return EasyStringUtils.quoteString(stringValue);
            }
        }
        return convertJDBCValueStrByType(dataValue);
    }

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return OracleValueProcessorFactory.getValueProcessor(dataValue.getDateTypeName()).convertSQLValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        String type = dataValue.getType();
        return OracleValueProcessorFactory.getValueProcessor(type).convertJDBCValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return OracleValueProcessorFactory.getValueProcessor(dataValue.getType()).convertJDBCValueStrByType(dataValue);
    }
}
