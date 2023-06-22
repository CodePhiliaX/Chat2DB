package ai.chat2db.server.test.domain.data.utils;

import java.util.concurrent.atomic.AtomicLong;

import ai.chat2db.server.test.domain.data.service.dialect.DialectProperties;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;

/**
 * 测试工具类
 *
 * @author Jiaju Zhuang
 */
public class TestUtils {

    public static final AtomicLong ATOMIC_LONG = new AtomicLong();

    /**
     * 一个全局唯一的long
     *
     * @return
     */
    public static long nextLong() {
        return ATOMIC_LONG.incrementAndGet();
    }

    /**
     * 如果默认值类似于 'DATA'
     * 则需要把'' 去掉
     *
     * @param defaultValue
     * @return
     */
    public static String unWrapperDefaultValue(String defaultValue) {
        if (defaultValue == null) {
            return null;
        }
        if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
            if (defaultValue.length() < 2) {
                return defaultValue;
            } else if (defaultValue.length() == 2) {
                return "";
            } else {
                return defaultValue.substring(1, defaultValue.length() - 1);
            }
        }
        return defaultValue;
    }

    public static void buildContext(DialectProperties dialectProperties,Long dataSourceId,Long consoleId){
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setUser(dialectProperties.getUsername());
        connectInfo.setConsoleId(consoleId);
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(dialectProperties.getPassword());
        connectInfo.setDbType(dialectProperties.getDbType());
        connectInfo.setUrl(dialectProperties.getUrl());
        connectInfo.setDatabase(dialectProperties.getDatabaseName());
        connectInfo.setConsoleOwn(false);
        Chat2DBContext.putContext(connectInfo);
    }

    public static void remove(){
        Chat2DBContext.removeContext();
    }
}
