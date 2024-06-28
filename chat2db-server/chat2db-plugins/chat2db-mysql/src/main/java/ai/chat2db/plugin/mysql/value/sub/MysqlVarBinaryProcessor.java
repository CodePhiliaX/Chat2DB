package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ch.qos.logback.core.model.processor.DefaultProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zgq
 * @date: 2024年06月03日 20:48
 */
@Slf4j
public class MysqlVarBinaryProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        // TODO: insert file
        return super.convertSQLValueByType(dataValue);
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        try {
            return dataValue.getBlobString();
        } catch (Exception e) {
            log.warn("convertJDBCValue error database: {} , error dataType: {} ",
                     Chat2DBContext.getDBConfig().getDbType(), dataValue.getType(), e);
            return super.convertJDBCValueByType(dataValue);
        }
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return dataValue.getBlobHexString();
    }
}

