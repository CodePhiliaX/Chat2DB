package ai.chat2db.plugin.mysql.value;

import ai.chat2db.plugin.mysql.value.factory.MysqlValueProcessorFactory;
import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 * @author: zgq
 * @date: 2024年05月24日 21:02
 * <br>
 *  TODO:
 *      attribute: [zerofill] example tinyint[5] zerofill 34->00034
 */
public class MysqlValueProcessor extends DefaultValueProcessor {
    public static final Set<String> FUNCTION_SET = Set.of("now()", "default");


    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            // mysql -> example: [date]->0000-00-00
            String stringValue = dataValue.getStringValue();
            if (Objects.nonNull(stringValue)) {
                return stringValue;
            }
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
    public String getJdbcValueString(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            // mysql -> example: [date]->0000-00-00
            String stringValue = dataValue.getStringValue();
            if (Objects.nonNull(stringValue)) {
                return EasyStringUtils.escapeAndQuoteString(stringValue);
            }
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
        if (FUNCTION_SET.contains(dataValue.getValue().toLowerCase())) {
            return dataValue.getValue();
        }
        return MysqlValueProcessorFactory.getValueProcessor(dataValue.getDateTypeName()).convertSQLValueByType(dataValue);
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        String type = dataValue.getType();
        return MysqlValueProcessorFactory.getValueProcessor(type).convertJDBCValueByType(dataValue);
    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        String type = dataValue.getType();
        return MysqlValueProcessorFactory.getValueProcessor(type).convertJDBCValueStrByType(dataValue);
    }
}
