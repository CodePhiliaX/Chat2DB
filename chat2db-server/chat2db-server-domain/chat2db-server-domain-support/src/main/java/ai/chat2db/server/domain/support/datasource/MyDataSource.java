/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.datasource;

import ai.chat2db.server.domain.support.enums.DriverTypeEnum;
import ai.chat2db.server.domain.support.sql.ConnectInfo;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author jipengfei
 * @version : MyDataSource.java
 */
public class MyDataSource {

    public ConnectInfo getConnectInfo() {
        return connectInfo;
    }

    private ConnectInfo connectInfo;

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    private HikariDataSource hikariDataSource;

    public MyDataSource(ConnectInfo connectInfo, HikariDataSource hikariDataSource) {
        this.connectInfo = connectInfo;
        this.hikariDataSource = hikariDataSource;
    }
}