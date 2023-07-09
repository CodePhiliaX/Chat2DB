
package ai.chat2db.spi.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.exception.ConnectionException;
import ai.chat2db.spi.DBManage;
import ai.chat2db.spi.MetaData;
import ai.chat2db.spi.Plugin;
import ai.chat2db.spi.config.DBConfig;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.model.SSHInfo;
import ai.chat2db.spi.ssh.SSHManager;
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
        ConnectInfo connectInfo = getConnectInfo();
        Connection connection = connectInfo.getConnection();
        if (connection == null) {
            connection = setConnectInfoThreadLocal(connectInfo);
        }
        return connection;
    }

    /**
     * 设置context
     *
     * @param info
     */
    public static void putContext(ConnectInfo info) {
        CONNECT_INFO_THREAD_LOCAL.set(info);
    }

    private static Connection setConnectInfoThreadLocal(ConnectInfo connectInfo) {
        synchronized (connectInfo) {
            Connection connection = connectInfo.getConnection();
            if (connection != null) {
                return connection;
            }
            Session session = null;
            SSHInfo ssh = connectInfo.getSsh();
            String url = connectInfo.getUrl();
            String host = connectInfo.getHost();
            String port = connectInfo.getPort() + "";
            try {
                ssh.setRHost(host);
                ssh.setRPort(port);
                session = getSession(ssh);
                if (session != null) {
                    url = url.replace(host, "127.0.0.1").replace(port, ssh.getLocalPort());
                }
            }catch (Exception e){
                throw new ConnectionException("connection.ssh.error",null,e);
            }
            try {
                DriverConfig config = connectInfo.getDriverConfig();
                if (config == null) {
                    config = getDefaultDriverConfig(connectInfo.getDbType());
                    connectInfo.setDriverConfig(config);
                }

                connection = IDriverManager.getConnection(url, connectInfo.getUser(), connectInfo.getPassword(),
                    connectInfo.getDriverConfig(), connectInfo.getExtendMap());

            } catch (Exception e1) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                    }
                }
                if (session != null) {
                    try {
                        session.delPortForwardingL(Integer.parseInt(ssh.getLocalPort()));
                    } catch (JSchException e) {
                    }
                    try {
                        session.disconnect();
                    } catch (Exception e) {
                    }
                }
                throw new BusinessException("connection.error",null,e1);
            }
            connectInfo.setSession(session);
            connectInfo.setConnection(connection);
            if (StringUtils.isNotBlank(connectInfo.getDatabaseName())) {
                PLUGIN_MAP.get(getConnectInfo().getDbType()).getDBManage().connectDatabase(
                    connectInfo.getDatabaseName());
            }
            return connection;
        }
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
            CONNECT_INFO_THREAD_LOCAL.remove();
        }
    }

}