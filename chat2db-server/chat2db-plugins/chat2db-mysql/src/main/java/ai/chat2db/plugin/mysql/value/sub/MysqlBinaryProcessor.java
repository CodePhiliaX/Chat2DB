package ai.chat2db.plugin.mysql.value.sub;

import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;

/**
 * @author: zgq
 * @date: 2024年06月03日 19:43
 */
public class MysqlBinaryProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return dataValue.getBlobHexString();
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getBlobHexString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return dataValue.getBlobHexString();
    }
}
