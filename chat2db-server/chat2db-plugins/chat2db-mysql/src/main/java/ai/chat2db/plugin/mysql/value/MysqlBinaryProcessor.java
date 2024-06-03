package ai.chat2db.plugin.mysql.value;

import ai.chat2db.plugin.mysql.value.template.MysqlDmlValueTemplate;
import ai.chat2db.spi.jdbc.DefaultValueProcessor;
import ai.chat2db.spi.model.JDBCDataValue;
import ai.chat2db.spi.model.SQLDataValue;
import com.google.common.io.BaseEncoding;

/**
 * @author: zgq
 * @date: 2024年06月03日 19:43
 */
public class MysqlBinaryProcessor extends DefaultValueProcessor {

    @Override
    public String convertSQLValueByType(SQLDataValue dataValue) {
        return wrap(BaseEncoding.base16().encode(dataValue.getValue().getBytes()));
    }


    @Override
    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return wrap(BaseEncoding.base16().encode(dataValue.getBytes()));
    }


    @Override
    public String convertJDBCValueStrByType(JDBCDataValue dataValue) {
        return convertJDBCValueByType(dataValue);
    }

    private String wrap(String value) {
        return String.format(MysqlDmlValueTemplate.BINARY_TEMPLATE, value);
    }
}
