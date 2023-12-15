package ai.chat2db.server.web.api.controller.ncx.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * DataBaseType
 *
 * @author lzy
 **/
@Getter
public enum DataBaseType {
    /**
     * MYSQL
     */
    MYSQL("jdbc:mysql://%s:%s"),
    /**
     * ORACLE
     */
    ORACLE("jdbc:oracle:thin:@%s:%s:XE"),
    /**
     * SQL_SERVER
     */
    SQLSERVER("jdbc:sqlserver://%s:%s"),
    /**
     * SQL_SERVER
     */
    SQLITE("jdbc:sqlite:%s"),
    /**
     * POSTGRESQL
     **/
    POSTGRESQL("jdbc:postgresql://%s:%s"),
    /**
     * DB2
     **/
    DB2("jdbc:db2://%s:%s"),
    /**
     * Mariadb
     **/
    MARIADB("jdbc:mariadb://%s:%s"),
    /**
     * DM
     **/
    DM("jdbc:dm://%s:%s"),
    /**
     * KINGBASE
     **/
    KINGBASE("jdbc:kingbase8://%s:%s"),
    /**
     * Presto
     **/
    PRESTO("jdbc:presto://%s:%s"),
    /**
     * OceanBase
     **/
    OCEANBASE("jdbc:oceanbase://%s:%s"),
    /**
     * Hive
     **/
    HIVE("jdbc:hive2://%s:%s"),
    /**
     * ClickHouse
     **/
    CLICKHOUSE("jdbc:clickhouse://%s:%s");

    private String urlString;

    DataBaseType(String urlString) {
        this.urlString = urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public static DataBaseType matchType(String value) {
        if (StringUtils.isNotEmpty(value)) {
            for (DataBaseType dataBase : DataBaseType.values()) {
                //kingbase -> kingbase8
                if (value.toUpperCase().contains(dataBase.name())) {
                    return dataBase;
                }
            }
        }
        return null;
    }

}
