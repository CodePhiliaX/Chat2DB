/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.datasource;

import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.alibaba.dbhub.server.domain.support.enums.DriverTypeEnum;
import com.alibaba.dbhub.server.domain.support.sql.ConnectInfo;
import com.alibaba.dbhub.server.domain.support.sql.IDriverManager;
import com.alibaba.fastjson2.JSON;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

/**
 * @author jipengfei
 * @version : DataSourceManger.java
 */
@Slf4j
public class DataSourceManger {

    protected static final ConcurrentHashMap<String, MyDataSource> DATA_SOURCE_MAP = new ConcurrentHashMap();

    public static DataSource getDataSource(ConnectInfo connectInfo) {
        String key = connectInfo.getDataSourceId().toString();
        MyDataSource dataSource = DATA_SOURCE_MAP.get(key);
        if (dataSource != null) {
            if (!connectInfo.equals(dataSource.getConnectInfo())) {
                try {
                    dataSource.getHikariDataSource().close();
                    DATA_SOURCE_MAP.remove(key);
                } catch (Exception e) {
                }
            } else {
                return dataSource.getHikariDataSource();
            }
        }
        synchronized (key) {
            dataSource = DATA_SOURCE_MAP.get(key);
            if (dataSource != null) {
                return dataSource.getHikariDataSource();
            } else {
                try {
                    dataSource = createDataSource(connectInfo);
                    DATA_SOURCE_MAP.put(key, dataSource);
                    return dataSource.getHikariDataSource();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static MyDataSource createDataSource(ConnectInfo connectInfo) throws Exception {
        DriverTypeEnum driverTypeEnum = DriverTypeEnum.getDriver(connectInfo.getDbType(), connectInfo.getJdbc());
        ClassLoader classLoader = IDriverManager.getClassLoader(driverTypeEnum);
        //IDataSource dataSource = new IDataSource(connectInfo, driverTypeEnum, classLoader);
        //dataSource.setName(connectInfo.getAlias());
        //dataSource.setDriverClassLoader(classLoader);
        //dataSource.setDriverClassName(driverTypeEnum.getDriverClass());
        //dataSource.setUrl(connectInfo.getUrl());
        //dataSource.setInitialSize(2);
        //dataSource.setMinIdle(0);
        //dataSource.setMaxActive(5);
        //dataSource.setMaxWait(3000L);
        //dataSource.setMinEvictableIdleTimeMillis(300000L);
        //dataSource.setUsername(connectInfo.getUser());
        //dataSource.setPassword(connectInfo.getPassword());
        //dataSource.setConnectionErrorRetryAttempts(2);
        //dataSource.setBreakAfterAcquireFailure(true);
        //dataSource.setRemoveAbandoned(true);
        //dataSource.setRemoveAbandonedTimeout(1800);
        //dataSource.setTestOnBorrow(true);
        //dataSource.setValidationQuery("select 1");
        //if (!ObjectUtils.isEmpty(connectInfo.getExtendMap())) {
        //    Properties properties = new Properties();
        //    properties.putAll(connectInfo.getExtendMap());
        //    dataSource.setConnectProperties(properties);
        //}
        //return dataSource;
        Thread.currentThread().setContextClassLoader(classLoader);
        log.info("createDataSource classLoader  hashCode:{}"+ classLoader.hashCode());
        log.info("createDataSource classLoader url :{}"+ JSON.toJSONString (((URLClassLoader)classLoader).getURLs()));
        HikariDataSource myDataSource = (HikariDataSource)classLoader.loadClass("com.zaxxer.hikari.HikariDataSource")
            .newInstance();
        myDataSource.setDriverClassName(driverTypeEnum.getDriverClass());
        myDataSource.setJdbcUrl(connectInfo.getUrl());
        myDataSource.setUsername(connectInfo.getUser());
        myDataSource.setPassword(connectInfo.getPassword());
        myDataSource.setAutoCommit(true);
        myDataSource.setConnectionTimeout(30000);
        myDataSource.setIdleTimeout(600000);
        myDataSource.setMaximumPoolSize(5);
        myDataSource.setMinimumIdle(1);
        myDataSource.setPoolName(connectInfo.getAlias());
        myDataSource.setConnectionTestQuery("select 1");
        if (!ObjectUtils.isEmpty(connectInfo.getExtendMap())) {
            Properties properties = new Properties();
            properties.putAll(connectInfo.getExtendMap());
            myDataSource.setDataSourceProperties(properties);
        }
        Connection connection = myDataSource.getConnection();
        if (connection != null) {connection.close();}
        return new MyDataSource(connectInfo, myDataSource);
    }

}