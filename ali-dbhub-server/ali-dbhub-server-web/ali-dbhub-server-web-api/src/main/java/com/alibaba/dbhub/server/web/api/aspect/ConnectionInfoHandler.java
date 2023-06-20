/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.aspect;

import com.alibaba.dbhub.server.domain.api.model.DataSource;
import com.alibaba.dbhub.server.domain.api.service.DataSourceService;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;
import com.alibaba.dbhub.server.domain.support.sql.ConnectInfo;
import com.alibaba.dbhub.server.domain.support.sql.DbhubContext;
import com.alibaba.dbhub.server.tools.base.excption.SystemException;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jipengfei
 * @version : ConnectionInfoHandler.java
 */
@Component
@Aspect
@Slf4j
public class ConnectionInfoHandler {

    @Autowired
    private DataSourceService dataSourceService;


    @Around("within(@com.alibaba.dbhub.server.web.api.aspect.ConnectionInfoAspect *)")
    public Object connectionInfoHandler(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object[] params = proceedingJoinPoint.getArgs();
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof DataSourceConsoleRequestInfo) {
                        Long dataSourceId = ((DataSourceConsoleRequestInfo)param).getDataSourceId();
                        Long consoleId = ((DataSourceConsoleRequestInfo)param).getConsoleId();
                        String database = ((DataSourceConsoleRequestInfo)param).getDatabaseName();
                        DbhubContext.putContext(toInfo(dataSourceId, database, consoleId));
                    } else if (param instanceof DataSourceBaseRequestInfo) {
                        Long dataSourceId = ((DataSourceBaseRequestInfo)param).getDataSourceId();
                        String database = ((DataSourceBaseRequestInfo)param).getDatabaseName();
                        DbhubContext.putContext(toInfo(dataSourceId, database));
                    }
                }
            }
            return proceedingJoinPoint.proceed();
        } finally {
            DbhubContext.removeContext();
        }
    }

    public ConnectInfo toInfo(Long dataSourceId, String database, Long consoleId) {
        DataResult<DataSource> result = dataSourceService.queryById(dataSourceId);
        DataSource dataSource = result.getData();
        if (!result.success() && dataSource != null) {
            throw new SystemException("dataSourceId ERROR");
        }
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setAlias(dataSource.getAlias());
        connectInfo.setUser(dataSource.getUserName());
        connectInfo.setConsoleId(consoleId);
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(dataSource.getPassword());
        connectInfo.setDbType(DbTypeEnum.getByName(dataSource.getType()));
        connectInfo.setUrl(dataSource.getUrl());
        connectInfo.setDatabase(database);
        connectInfo.setConsoleOwn(false);
        connectInfo.setDriver(dataSource.getDriver());
        connectInfo.setSsh(dataSource.getSsh());
        connectInfo.setSsl(dataSource.getSsl());
        connectInfo.setJdbc(dataSource.getJdbc());
        connectInfo.setExtendInfo(dataSource.getExtendInfo());
        connectInfo.setUrl(dataSource.getUrl());
        connectInfo.setPort(dataSource.getPort() != null ? Integer.parseInt(dataSource.getPort()) : null);
        connectInfo.setHost(dataSource.getHost());
        return connectInfo;
    }

    public ConnectInfo toInfo(Long dataSourceId, String database) {
        return toInfo(dataSourceId, database, null);
    }
}