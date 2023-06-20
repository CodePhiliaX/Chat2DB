/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.datasource;

import java.io.Serial;
import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.enums.DriverTypeEnum;
import com.alibaba.dbhub.server.domain.support.model.SSHInfo;
import com.alibaba.dbhub.server.domain.support.sql.ConnectInfo;
import com.alibaba.dbhub.server.domain.support.sql.SSHManager;
import com.alibaba.druid.pool.DruidDataSource;

import com.jcraft.jsch.Session;

/**
 * @author jipengfei
 * @version : DbhubDataSource.java
 */
public class IDataSource extends DruidDataSource {
    @Serial
    private static final long serialVersionUID = -232274227856574115L;

    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    private ConnectInfo connectInfo;

    public IDataSource(ConnectInfo connectInfo, DriverTypeEnum driverTypeEnum, ClassLoader classLoader) {
        this.connectInfo = connectInfo;

    }

    @Override
    public void init() throws SQLException {
        if (inited) {
            return;
        }
        connectSession();
        super.init();
        inited = true;
    }

    private void connectSession() {
        SSHInfo ssh = connectInfo.getSsh();
        if (ssh != null && ssh.isUse()) {
            Session session = SSHManager.getSSHSession(ssh);
            String url = connectInfo.getUrl();
            url = url.replace(connectInfo.getHost(), "127.0.0.1").replace(connectInfo.getPort() + "",
                ssh.getLocalPort());
            setUrl(url);
        }
    }
}