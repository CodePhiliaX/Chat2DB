
package ai.chat2db.spi.config;

import lombok.Data;

import java.util.List;

/**
 * @author jipengfei
 * @version : DBConfig.java
 */
@Data
public class DBConfig {

    /**
     * MYSQL POSTGRESQL ...
     */
    private String dbType;

    /**
     * Mysql PostgreSQL ...
     */
    private String name;

    /**
     * defaultDriverConfig
     */
    private DriverConfig defaultDriverConfig;

    /**
     * List of supported drivers
     */
    private List<DriverConfig> driverConfigList;


    /**
     * 建表语句
     */
    private String simpleCreateTable;

    /**
     * 修改表结构
     */
    private String simpleAlterTable;
}