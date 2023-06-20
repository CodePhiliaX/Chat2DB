/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jipengfei
 * @version : DriverTypeEnum.java
 */
public enum DriverTypeEnum {
    /**
     * MySQL
     */
    MYSQL_DRIVER(DbTypeEnum.MYSQL, "com.mysql.cj.jdbc.Driver", "mysql-connector-java-8.0.30.jar","8.0"),

    /**
     * MySQL 5.1版本
     */
    MYSQL_DRIVER_5_1(DbTypeEnum.MYSQL, "com.mysql.jdbc.Driver", "mysql-connector-java-5.1.47.jar","5.0"),

    /**
     * PostgreSQL
     */
    POSTGRESQL_DRIVER(DbTypeEnum.POSTGRESQL, "org.postgresql.Driver", "postgresql-42.5.1.jar",""),

    /**
     * Oracle
     */
    ORACLE_DRIVER(DbTypeEnum.ORACLE, "oracle.jdbc.driver.OracleDriver", "ojdbc8-19.3.0.0.jar,orai18n-19.3.0.0.jar",""),

    /**
     * SQLServer
     */
    SQLSERVER_DRIVER(DbTypeEnum.SQLSERVER, "com.microsoft.sqlserver.jdbc.SQLServerDriver",
        "mssql-jdbc-11.2.1.jre17.jar",""),

    /**
     * SQLite
     */
    SQLITE_DRIVER(DbTypeEnum.SQLITE, "org.sqlite.JDBC", "sqlite-jdbc-3.39.3.0.jar",""),

    /**
     * H2
     */
    H2_DRIVER(DbTypeEnum.H2, "org.h2.Driver", "h2-2.1.214.jar",""),

    /**
     * ADB MySQL
     */
    ADB_POSTGRESQL_DRIVER(DbTypeEnum.ADB_POSTGRESQL, "org.postgresql.Driver", "",""),

    /**
     * ClickHouse
     */
    CLICKHOUSE_DRIVER(DbTypeEnum.CLICKHOUSE, "com.clickhouse.jdbc.ClickHouseDriver", "clickhouse-jdbc-0.3.2-patch8-http.jar",""),

    /**
     * OceanBase
     */
    OCEANBASE_DRIVER(DbTypeEnum.OCEANBASE, "com.oceanbase.jdbc.Driver", "oceanbase-client-2.4.2.jar",""),

    /**
     * DB2
     */
    DB2_DRIVER(DbTypeEnum.DB2, "com.ibm.db2.jcc.DB2Driver", "db2jcc4_4.26.14.jar",""),

    /**
     * MMARIADB
     */
    MARIADB_DRIVER(DbTypeEnum.MARIADB, "org.mariadb.jdbc.Driver", "mariadb-java-client-3.0.8.jar",""),



    /**
     * DM_DRIVER
     */
    DM_DRIVER(DbTypeEnum.DM, "dm.jdbc.driver.DmDriver", "DmJdbcDriver18-8.1.2.141.jar",""),


    /**
     * PRESTO_DRIVER
     */
    PRESTO_DRIVER(DbTypeEnum.PRESTO, "com.facebook.presto.jdbc.PrestoDriver", "presto-jdbc-0.245.1.jar",""),

    /**
     * KINGBASE_DRIVER
     * com.kingbase8.Driver
     */
    KINGBASE_DRIVER(DbTypeEnum.KINGBASE, "com.kingbase8.Driver", "kingbase8-8.6.0.jar",""),

    /**
     * HIVE_DRIVER
     * org.apache.hive.jdbc.HiveDriver
     */
    HIVE_DRIVER(DbTypeEnum.HIVE, "org.apache.hive.jdbc.HiveDriver", "hive-jdbc-3.1.2-standalone.jar",""),

    /**
     * REDIS_DRIVER
     */
    REDIS_DRIVER(DbTypeEnum.REDIS, "jdbc.RedisDriver", "redis-jdbc-driver-1.3.jar",""),
    /**
     * MONGODB_DRIVER
     * com.dbschema.MongoJdbcDriver
     */
    MONGODB_DRIVER(DbTypeEnum.MONGODB, "com.dbschema.MongoJdbcDriver", "mongo-jdbc-standalone-1.18.jar","");

    final DbTypeEnum dbTypeEnum;

    final String driverClass;

    final String jar;

    final String jdbc;

    DriverTypeEnum(DbTypeEnum dbTypeEnum, String driverClass, String jar, String jdbc) {
        this.dbTypeEnum = dbTypeEnum;
        this.driverClass = driverClass;
        this.jar = jar;
        this.jdbc = jdbc;
    }

    public static DriverTypeEnum getDriver(DbTypeEnum dbTypeEnum, String jdbc) {
        for (DriverTypeEnum driverTypeEnum : DriverTypeEnum.values()) {
            if (driverTypeEnum.dbTypeEnum.equals(dbTypeEnum)) {
                if (StringUtils.isBlank(jdbc) || driverTypeEnum.jdbc.equals(jdbc)) {
                    return driverTypeEnum;
                }
            }
        }
        return null;
    }

    /**
     * Getter method for property <tt>dbTypeEnum</tt>.
     *
     * @return property value of dbTypeEnum
     */
    public DbTypeEnum getDbTypeEnum() {
        return dbTypeEnum;
    }

    /**
     * Getter method for property <tt>jar</tt>.
     *
     * @return property value of jar
     */
    public String getJar() {
        return jar;
    }

    /**
     * Getter method for property <tt>driverClass</tt>.
     *
     * @return property value of driverClass
     */
    public String getDriverClass() {
        return driverClass;
    }

}