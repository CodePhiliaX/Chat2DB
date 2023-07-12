package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.spi.config.DBConfig;

public interface JdbcDriverService {

    /**
     * Query the driver list of the current DB
     *
     * @param dbType
     * @return
     */
    DataResult<DBConfig> getDrivers(String dbType);

    /**
     * Upload the driver
     *
     * @param dbType
     * @param jdbcDriverClass
     * @param jdbcDriver
     * @return
     */
    ActionResult upload(String dbType, String jdbcDriverClass, String jdbcDriver);

    /**
     * Upload the driver
     *
     * @param dbType
     * @return
     */
    ActionResult download(String dbType);
}
