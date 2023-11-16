
package ai.chat2db.spi.config;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author jipengfei
 * @version : DBConfig.java
 */
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


    private boolean supportDatabase;


    private boolean supportSchema;

    public boolean isSupportDatabase() {
        return supportDatabase;
    }

    public void setSupportDatabase(boolean supportDatabase) {
        this.supportDatabase = supportDatabase;
    }

    public boolean isSupportSchema() {
        return supportSchema;
    }

    public void setSupportSchema(boolean supportSchema) {
        this.supportSchema = supportSchema;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DriverConfig getDefaultDriverConfig() {
        if (this.defaultDriverConfig != null) {
            return this.defaultDriverConfig;
        } else {
            if (!CollectionUtils.isEmpty(driverConfigList)) {
                for (DriverConfig driverConfig : driverConfigList) {
                    if (driverConfig.isDefaultDriver()) {
                        return driverConfig;
                    }
                }
                return driverConfigList.get(0);
            }
        }
        return null;
    }

    public void setDefaultDriverConfig(DriverConfig defaultDriverConfig) {
        this.defaultDriverConfig = defaultDriverConfig;
    }

    public List<DriverConfig> getDriverConfigList() {
        return driverConfigList;
    }

    public void setDriverConfigList(List<DriverConfig> driverConfigList) {
        this.driverConfigList = driverConfigList;
        if (!CollectionUtils.isEmpty(driverConfigList)) {
            for (DriverConfig driverConfig : driverConfigList) {
                if (driverConfig.isDefaultDriver()) {
                    this.defaultDriverConfig = driverConfig;
                    break;
                }
            }
        }
    }

    public String getSimpleCreateTable() {
        return simpleCreateTable;
    }

    public void setSimpleCreateTable(String simpleCreateTable) {
        this.simpleCreateTable = simpleCreateTable;
    }

    public String getSimpleAlterTable() {
        return simpleAlterTable;
    }

    public void setSimpleAlterTable(String simpleAlterTable) {
        this.simpleAlterTable = simpleAlterTable;
    }
}