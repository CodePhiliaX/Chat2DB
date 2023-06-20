/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.enums.DriverTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : DbhubContext.java
 */
@Slf4j
public class DbhubContext {

    private static final ThreadLocal<ConnectInfo> CONNECT_INFO_THREAD_LOCAL = new ThreadLocal<>();

    public static List<String> JDBC_JAR_DOWNLOAD_URL_LIST;

    /**
     * 获取当前线程的ContentContext
     *
     * @return
     */
    public static ConnectInfo getConnectInfo() {
        return CONNECT_INFO_THREAD_LOCAL.get();
    }

    public static MetaSchema getMetaSchema() {
        return getConnectInfo().getDbType().metaSchema();
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
                SQLExecutor.getInstance().connectDatabase(info.getDatabaseName());
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
            connection = getConnect(url, host, port, connectInfo.getUser(),
                connectInfo.getPassword(), connectInfo.getDbType(),
                connectInfo.getJdbc(), ssh, connectInfo.getExtendMap());
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
        String userName, String password, DbTypeEnum dbType,
        String jdbc, SSHInfo ssh, Map<String, Object> properties) throws SQLException {
        // 创建连接
        return IDriverManager.getConnection(url, userName, password,
            DriverTypeEnum.getDriver(dbType, jdbc), properties);

    }

    private static Session getSession(SSHInfo ssh) {
        Session session = null;
        if (ssh!=null && ssh.isUse()) {
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