package ai.chat2db.plugin.snowflake;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.jdbc.DefaultDBManage;
import ai.chat2db.spi.model.KeyValue;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
import ai.chat2db.spi.sql.SQLExecutor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SnowFlakeDBManage extends DefaultDBManage implements DBManage {

    @Override
    public Connection getConnection(ConnectInfo connectInfo) {
        List<KeyValue> extendInfo = connectInfo.getExtendInfo();
        if (StringUtils.isNotBlank(connectInfo.getDatabaseName())) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("db");
            keyValue.setValue(connectInfo.getDatabaseName());
            extendInfo.add(keyValue);
        }
        if (StringUtils.isNotBlank(connectInfo.getSchemaName())) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("schema");
            keyValue.setValue(connectInfo.getSchemaName());
            extendInfo.add(keyValue);
        }
        KeyValue keyValue = new KeyValue();
        keyValue.setKey("JDBC_QUERY_RESULT_FORMAT");
        keyValue.setValue("JSON");
        extendInfo.add(keyValue);
        connectInfo.setExtendInfo(extendInfo);
        return super.getConnection(connectInfo);
    }


    @Override
    public void connectDatabase(Connection connection, String database) {
        if (StringUtils.isEmpty(database)) {
            return;
        }
        ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
        if (ObjectUtils.anyNull(connectInfo) || StringUtils.isEmpty(connectInfo.getSchemaName())) {
            try {
                SQLExecutor.getInstance().execute(connection, "USE DATABASE \"" + database + "\";");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                SQLExecutor.getInstance().execute(connection, "USE SCHEMA \"" + database + "\"." + connectInfo.getSchemaName() + ";");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
