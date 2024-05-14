package ai.chat2db.spi.sql;

import lombok.extern.slf4j.Slf4j;
import org.h2.engine.ConnectionInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConnectionPool {

    private static ConcurrentHashMap<String, ConnectInfo> CONNECTION_MAP = new ConcurrentHashMap<>();

    static {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 10);
                    CONNECTION_MAP.forEach((k, v) -> {
                        if (v.getLastAccessTime().getTime() + 1000 * 60 * 60 < System.currentTimeMillis()) {
                            try {
                                Connection connection = v.getConnection();
                                if (connection != null ) {
                                    connection.close();
                                    CONNECTION_MAP.remove(k);
                                }
                            } catch (SQLException e) {
                                log.error("close connection error", e);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    log.error("close connection error", e);
                }
            }
        }).start();

    }


    public static ConnectInfo getAndRemove(String key) {
        return CONNECTION_MAP.computeIfPresent(key, (k, v) -> {
            CONNECTION_MAP.remove(k); // 从 Map 中移除
            return v;      // 返回值
        });
    }

    public static Connection getConnection(ConnectInfo connectInfo) {

        try {
            Connection connection = connectInfo.getConnection();
            if (connection != null && !connection.isClosed()) {
                log.info("get connection from loacl");
                return connection;
            }
            ConnectInfo cache = getAndRemove(connectInfo.key());
            if (cache != null) {
                connection = cache.getConnection();
                if (connection != null && !connection.isClosed()) {
                    log.info("get connection from cache");
                    connectInfo.setConnection(connection);
                    return connection;
                }
            }
            synchronized (connectInfo) {
                connection = connectInfo.getConnection();
                try {
                    if (connection != null && !connection.isClosed()) {
                        log.info("get connection from cache");
                        return connection;
                    } else {
                        log.info("get connection from db begin");
                        connection = Chat2DBContext.getDBManage().getConnection(connectInfo);
                        log.info("get connection from db end");
                    }
                } catch (SQLException e) {
                    log.error("get connection error", e);
                    log.info("get connection from db begin2");
                    connection = Chat2DBContext.getDBManage().getConnection(connectInfo);
                    log.info("get connection from db end2");
                }
                connectInfo.setConnection(connection);
                return connection;
            }
        } catch (SQLException e) {
            log.error("get connection error", e);
        }
        return null;
    }

    public static void close(ConnectInfo connectInfo) {
        String key = connectInfo.key();
        synchronized (key) {
            ConnectInfo cache = getAndRemove(key);
            if (cache != null) {
                Connection connection = cache.getConnection();
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        log.error("close connection error", e);
                    }
                }
            }
            connectInfo.setLastAccessTime(new Date());
            CONNECTION_MAP.put(key, connectInfo);
        }
    }
}
