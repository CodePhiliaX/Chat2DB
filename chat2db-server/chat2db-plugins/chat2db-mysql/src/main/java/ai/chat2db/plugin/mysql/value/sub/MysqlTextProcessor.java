package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * @author: zgq
 * @date: 2024年06月05日 0:11
 */
@Slf4j
public class MysqlTextProcessor extends DefaultValueProcessor {


    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(dataValue.getValue());
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return getClobString(dataValue, super::convertJDBCValueByType);
    }

    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return wrap(getClobString(dataValue, super::convertJDBCValueStrByType));
    }

    private String getClobString(JDBCDataValue dataValue, Function<JDBCDataValue, String> function) {
        try {
            return dataValue.getClobString();
        } catch (Exception e) {
            log.warn("convertJDBCValue error database: {} , error dataType: {} ",
                     Chat2DBContext.getDBConfig().getDbType(), dataValue.getType(), e);
            return function.apply(dataValue);
        }
    }

    private String wrap(String value) {
        return EasyStringUtils.escapeAndQuoteString(value);
    }
}
