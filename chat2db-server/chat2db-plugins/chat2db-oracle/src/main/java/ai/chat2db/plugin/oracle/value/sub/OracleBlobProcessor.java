package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zgq
 * @date: 2024年06月05日 20:06
 */
@Slf4j
public class OracleBlobProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return dataValue.getBlobHexString();
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        try {
            return dataValue.getBlobString();
        } catch (Exception e) {
            log.warn("convertJDBCValueByType error database: {} , error dataType: {} ",
                     Chat2DBContext.getDBConfig().getDbType(), dataValue.getType(), e);
            return super.convertJDBCValueByType(dataValue);
        }

    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return dataValue.getBlobHexString();
    }
}
