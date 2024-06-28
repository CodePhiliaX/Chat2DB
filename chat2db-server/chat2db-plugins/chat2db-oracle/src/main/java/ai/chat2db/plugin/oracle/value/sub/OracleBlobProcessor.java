package ai.chat2db.plugin.oracle.value.sub;

import ai.chat2db.server.tools.common.util.EasyStringUtils;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: zgq
 * @date: 2024年06月05日 20:06
 */
@Slf4j
public class OracleBlobProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getBlobHexString());
    }

    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getBlobString();
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return EasyStringUtils.quoteString(dataValue.getBlobHexString());
    }
}
