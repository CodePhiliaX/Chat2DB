/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.spi.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.model.SSHInfo;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : Chat2DBContext.java
 */
@Slf4j
public class Chat2DBContext {

    private static final ThreadLocal<ConnectInfo> CONNECT_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static List<String> JDBC_JAR_DOWNLOAD_URL_LIST;

    public static Map<String, Plugin> PLUGIN_MAP = new ConcurrentHashMap<>();

    static {
        ServiceLoader<Plugin> s = ServiceLoader.load(Plugin.class);
        Iterator<Plugin> iterator = s.iterator();
        while (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            PLUGIN_MAP.put(plugin.getDBConfig().getDbType(), plugin);
        }
    }

    public static DriverConfig getDefaultDriverConfig(String dbType) {
        return PLUGIN_MAP.get(dbType).getDBConfig().getDefaultDriverConfig();
    }

    /**
     * 获取当前线程的ContentContext
     *
     * @return
     */
    public static ConnectInfo getConnectInfo() {
        return CONNECT_INFO_THREAD_LOCAL.get();
    }

    public static MetaData getMetaData() {
        return PLUGIN_MAP.get(getConnectInfo().getDbType()).getMetaData();
    }

    public static DBConfig getDBConfig() {
        return PLUGIN_MAP.get(getConnectInfo().getDbType()).getDBConfig();
    }

    public static DBManage getDBManage() {
        return PLUGIN_MAP.get(getConnectInfo().getDbType()).getDBManage();
    }

    public static Connection getConnection() {
        return getConnectInfo().getConnection();
    }

    /**
     * 设置context
     *
     * @param info
     */
    public static void putContext(ConnectInfo info) {
        ConnectInfo connectInfo = CONNECT_INFO_THREAD_LOCAL.get();
        CONNECT_INFO_THREAD_LOCAL.set(info);
        if (connectInfo == null) {
            setConnectInfoThreadLocal(info);
            if (StringUtils.isNotBlank(info.getDatabaseName())) {
                PLUGIN_MAP.get(getConnectInfo().getDbType()).getDBManage().connectDatabase(info.getDatabaseName());
            }
        }
    }

    private static void setConnectInfoThreadLocal(ConnectInfo connectInfo) {
        Session session = null;
        Connection connection = null;
        SSHInfo ssh = connectInfo.getSsh();
        String url = connectInfo.getUrl();
        String host = connectInfo.getHost();
        String port = connectInfo.getPort() + "";
        try {
            session = getSession(ssh);
            if (session != null) {
                url = url.replace(host, "127.0.0.1").replace(port, ssh.getLocalPort());
            }
            DriverConfig config = connectInfo.getDriverConfig();
            if (config == null) {
                config = getDefaultDriverConfig(connectInfo.getDbType());
                connectInfo.setDriverConfig(config);
            }

            connection = getConnect(url, host, port, connectInfo.getUser(),
                    connectInfo.getPassword(), connectInfo.getDbType(),
                    connectInfo.getDriverConfig(), ssh, connectInfo.getExtendMap());
        } catch (Exception e1) {
            log.error("getConnect error", e1);
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("session close error", e);
                }
            }
            if (session != null) {
                try {
                    session.delPortForwardingL(Integer.parseInt(ssh.getLocalPort()));
                    session.disconnect();
                } catch (JSchException e) {
                    log.error("session close error", e);
                }
            }
            throw new RuntimeException("getConnect error", e1);
        }
        connectInfo.setSession(session);
        connectInfo.setConnection(connection);
    }

    /**
     * 测试数据库连接
     *
     * @param url      数据库连接
     * @param userName 用户名
     * @param password 密码
     * @param dbType   数据库类型
     * @return
     */
    private static Connection getConnect(String url, String host, String port,
                                         String userName, String password, String dbType,
                                         DriverConfig jdbc, SSHInfo ssh, Map<String, Object> properties) throws SQLException {
        // 创建连接
        return IDriverManager.getConnection(url, userName, password, jdbc, properties);

    }

    private static Session getSession(SSHInfo ssh) {
        Session session = null;
        if (ssh != null && ssh.isUse()) {
            session = SSHManager.getSSHSession(ssh);
        }
        return session;
    }

    /**
     * 设置context
     */
    public static void removeContext() {
        ConnectInfo connectInfo = CONNECT_INFO_THREAD_LOCAL.get();
        if (connectInfo != null) {
            Connection connection = connectInfo.getConnection();
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("close connection error", e);
            }
            Session session = connectInfo.getSession();
            if (session != null) {
                try {
                    session.delPortForwardingL(Integer.parseInt(connectInfo.getSsh().getLocalPort()));
                    session.disconnect();
                } catch (JSchException e) {
                    log.error("close session error", e);
                }
            }
            CONNECT_INFO_THREAD_LOCAL.remove();
        }
    }

}