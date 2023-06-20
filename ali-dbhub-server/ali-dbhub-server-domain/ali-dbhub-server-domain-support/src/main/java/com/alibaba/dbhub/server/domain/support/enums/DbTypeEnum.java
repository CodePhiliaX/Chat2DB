package com.alibaba.dbhub.server.domain.support.enums;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.clickhouse.ClickhouseMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.common.model.SpiExample;
import com.alibaba.dbhub.server.domain.support.dialect.db2.DB2MetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.dm.DMMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.h2.H2MetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.mariadb.MariaDBMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.mysql.MysqlMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.oceanbase.OceanBaseMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.oracle.OracleMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.postgresql.PostgresqlMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.sqlite.SQLiteMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.sqlserver.SqlServerMetaSchemaSupport;
import com.alibaba.dbhub.server.tools.base.enums.BaseEnum;

import lombok.Getter;

import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.H2_ALTER_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.H2_CREATE_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.MYSQL_ALTER_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.MYSQL_CREATE_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.ORACLE_ALTER_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.ORACLE_CREATE_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.PG_ALTER_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.PG_CREATE_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.SQLITE_ALTER_TABLE_SIMPLE;
import static com.alibaba.dbhub.server.domain.support.dialect.common.SQLKeyConst.SQLITE_CREATE_TABLE_SIMPLE;

/**
 * 数据类型
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum DbTypeEnum implements BaseEnum<String> {
    /**
     * MySQL
     */
    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "mysql-connector-j-8.0.33.jar"),

    /**
     * PostgreSQL
     */
    POSTGRESQL("PostgreSQL", "org.postgresql.Driver", "postgresql-42.5.1.jar"),

    /**
     * Oracle
     */
    ORACLE("Oracle", "oracle.jdbc.driver.OracleDriver", "ojdbc8-19.3.0.0.jar,orai18n-19.3.0.0.jar"),

    /**
     * SQLServer
     */
    SQLSERVER("SQLServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "mssql-jdbc-11.2.1.jre17.jar"),

    /**
     * SQLite
     */
    SQLITE("SQLite", "org.sqlite.JDBC", "sqlite-jdbc-3.39.3.0.jar"),

    /**
     * H2
     */
    H2("H2", "org.h2.Driver", "h2-2.1.214.jar"),

    /**
     * ADB MySQL
     */
    ADB_POSTGRESQL("PostgreSQL", "org.postgresql.Driver", ""),

    /**
     * ClickHouse
     */
    CLICKHOUSE("ClickHouse", "ru.yandex.clickhouse.ClickHouseDriver", "clickhouse-jdbc-0.4.1.jar"),

    /**
     * OceanBase
     */
    OCEANBASE("OceanBase", "com.oceanbase.jdbc.Driver", "oceanbase-client-2.4.2.jar"),

    /**
     * DB2
     */
    DB2("DB2", "com.ibm.db2.jcc.DB2Driver", ""),

    /**
     * MMARIADB
     */
    MARIADB("MariaDB", "org.mariadb.jdbc.Driver", "mariadb-java-client-3.0.8.jar"),

    /**
     * DM 达梦
     */
    DM("DM", "dm.jdbc.driver.DmDriver", "DmJdbcDriver18-8.1.2.141.jar");

    final String description;
    final String className;

    final String jar;

    private static Map<DbTypeEnum, MetaSchema> META_SCHEMA_MAP = new HashMap<>();

    static {
        META_SCHEMA_MAP.put(H2, new H2MetaSchemaSupport());
        META_SCHEMA_MAP.put(MYSQL, new MysqlMetaSchemaSupport());
        META_SCHEMA_MAP.put(POSTGRESQL, new PostgresqlMetaSchemaSupport());
        META_SCHEMA_MAP.put(ORACLE, new OracleMetaSchemaSupport());
        META_SCHEMA_MAP.put(SQLSERVER, new SqlServerMetaSchemaSupport());
        META_SCHEMA_MAP.put(SQLITE, new SQLiteMetaSchemaSupport());
        META_SCHEMA_MAP.put(OCEANBASE, new OceanBaseMetaSchemaSupport());
        META_SCHEMA_MAP.put(MARIADB, new MariaDBMetaSchemaSupport());
        META_SCHEMA_MAP.put(CLICKHOUSE, new ClickhouseMetaSchemaSupport());
        META_SCHEMA_MAP.put(DB2, new DB2MetaSchemaSupport());
        META_SCHEMA_MAP.put(DM, new DMMetaSchemaSupport());
    }

    DbTypeEnum(String description, String className, String jar) {
        this.description = description;
        this.className = className;
        this.jar = jar;
    }

    /**
     * 通过名称获取枚举
     *
     * @param name
     * @return
     */
    public static DbTypeEnum getByName(String name) {
        for (DbTypeEnum dbTypeEnum : DbTypeEnum.values()) {
            if (dbTypeEnum.name().equals(name)) {
                return dbTypeEnum;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    public MetaSchema metaSchema() {
        return META_SCHEMA_MAP.get(this);
    }

    public SpiExample example() {
        SpiExample SpiExample = null;
        switch (this) {
            case H2:
                SpiExample = SpiExample.builder().createTable(H2_CREATE_TABLE_SIMPLE).alterTable(H2_ALTER_TABLE_SIMPLE)
                    .build();
                break;
            case MYSQL:
                SpiExample = SpiExample.builder().createTable(MYSQL_CREATE_TABLE_SIMPLE).alterTable(
                    MYSQL_ALTER_TABLE_SIMPLE).build();
                break;
            case POSTGRESQL:
                SpiExample = SpiExample.builder().createTable(PG_CREATE_TABLE_SIMPLE).alterTable(PG_ALTER_TABLE_SIMPLE)
                    .build();
                break;
            case ORACLE:
                SpiExample = SpiExample.builder().createTable(ORACLE_CREATE_TABLE_SIMPLE).alterTable(
                    ORACLE_ALTER_TABLE_SIMPLE).build();
                break;
            case SQLSERVER:
                SpiExample = SpiExample.builder().createTable(ORACLE_CREATE_TABLE_SIMPLE).alterTable(
                    ORACLE_ALTER_TABLE_SIMPLE).build();
                break;
            case SQLITE:
                SpiExample = SpiExample.builder().createTable(SQLITE_CREATE_TABLE_SIMPLE).alterTable(
                    SQLITE_ALTER_TABLE_SIMPLE).build();
                break;
            case OCEANBASE:
                SpiExample = SpiExample.builder().createTable(MYSQL_CREATE_TABLE_SIMPLE).alterTable(
                    MYSQL_ALTER_TABLE_SIMPLE).build();
                break;
            case CLICKHOUSE:
                SpiExample = SpiExample.builder().createTable(MYSQL_CREATE_TABLE_SIMPLE).alterTable(
                    MYSQL_ALTER_TABLE_SIMPLE).build();
                break;
            case MARIADB:
                SpiExample = SpiExample.builder().createTable(MYSQL_CREATE_TABLE_SIMPLE).alterTable(
                    MYSQL_ALTER_TABLE_SIMPLE).build();
                break;
            case DB2:
                SpiExample = SpiExample.builder().createTable(MYSQL_CREATE_TABLE_SIMPLE).alterTable(
                    MYSQL_ALTER_TABLE_SIMPLE).build();
                break;
            case DM:
                SpiExample = SpiExample.builder().createTable(MYSQL_CREATE_TABLE_SIMPLE).alterTable(
                    MYSQL_ALTER_TABLE_SIMPLE).build();
                break;
            default:
        }
        return SpiExample;
    }

}
