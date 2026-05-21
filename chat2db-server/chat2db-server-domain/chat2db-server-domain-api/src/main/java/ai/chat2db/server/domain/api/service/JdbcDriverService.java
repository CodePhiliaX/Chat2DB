package ai.chat2db.server.domain.api.service;

import ai.chat2db.spi.config.DBConfig;

public interface JdbcDriverService {

    /**
     * Query the driver list of the current DB
     *
     * @param dbType
     * @return
     */
    DBConfig getDrivers(String dbType);

    /**
     * Upload the driver
     *
     * @param dbType
     * @param jdbcDriverClass
     * @param jdbcDriver
     * @return
     */
    void upload(String dbType, String jdbcDriverClass, String jdbcDriver);

    /**
     * Upload the driver
     *
     * @param dbType
     * @return
     */
    void download(String dbType);
}
