
package ai.chat2db.server.web.api.aspect;

import ai.chat2db.server.domain.api.model.DataSource;
import ai.chat2db.server.domain.api.service.DataSourceService;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.exception.ParamBusinessException;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;
import ai.chat2db.spi.config.DriverConfig;
import ai.chat2db.spi.sql.Chat2DBContext;
import ai.chat2db.spi.sql.ConnectInfo;
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

    @Around("within(@ai.chat2db.server.web.api.aspect.ConnectionInfoAspect *)")
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
                        Chat2DBContext.putContext(toInfo(dataSourceId, database, consoleId));
                    } else if (param instanceof DataSourceBaseRequestInfo) {
                        Long dataSourceId = ((DataSourceBaseRequestInfo)param).getDataSourceId();
                        String database = ((DataSourceBaseRequestInfo)param).getDatabaseName();
                        Chat2DBContext.putContext(toInfo(dataSourceId, database));
                    }
                }
            }
            return proceedingJoinPoint.proceed();
        } finally {
            Chat2DBContext.removeContext();
        }
    }

    public ConnectInfo toInfo(Long dataSourceId, String database, Long consoleId) {
        DataResult<DataSource> result = dataSourceService.queryById(dataSourceId);
        DataSource dataSource = result.getData();
        if (!result.success() || dataSource == null) {
            throw new ParamBusinessException("dataSourceId");
        }
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setAlias(dataSource.getAlias());
        connectInfo.setUser(dataSource.getUserName());
        connectInfo.setConsoleId(consoleId);
        connectInfo.setDataSourceId(dataSourceId);
        connectInfo.setPassword(dataSource.getPassword());
        connectInfo.setDbType(dataSource.getType());
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
        DriverConfig driverConfig = dataSource.getDriverConfig();
        if (driverConfig != null && driverConfig.notEmpty()) {
            connectInfo.setDriverConfig(driverConfig);
        }
        return connectInfo;
    }

    public ConnectInfo toInfo(Long dataSourceId, String database) {
        return toInfo(dataSourceId, database, null);
    }
}