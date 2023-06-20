package com.alibaba.dbhub.server.domain.support.enums;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.clickhouse.ClickhouseMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.common.model.SpiExample;
import com.alibaba.dbhub.server.domain.support.dialect.db2.DB2MetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.dm.DMMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.h2.H2MetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.hive.HiveMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.kingbase.KingBaseSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.mariadb.MariaDBMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.mongodb.MongodbMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.mysql.MysqlMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.oceanbase.OceanBaseMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.oracle.OracleMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.postgresql.PostgresqlMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.presto.PrestoMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.dialect.redis.RedisMetaSchemaSupport;
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
    MYSQL("MySQL"),

    /**
     * PostgreSQL
     */
    POSTGRESQL("PostgreSQL"),

    /**
     * Oracle
     */
    ORACLE("Oracle"),

    /**
     * SQLServer
     */
    SQLSERVER("SQLServer"),

    /**
     * SQLite
     */
    SQLITE("SQLite"),

    /**
     * H2
     */
    H2("H2"),

    /**
     * ADB MySQL
     */
    ADB_POSTGRESQL("PostgreSQL"),

    /**
     * ClickHouse
     */
    CLICKHOUSE("ClickHouse"),

    /**
     * OceanBase
     */
    OCEANBASE("OceanBase"),

    /**
     * DB2
     */
    DB2("DB2"),

    /**
     * MMARIADB
     */
    MARIADB("MariaDB"),

    /**
     * DM 达梦
     */
    DM("DM"),


    /**
     * MONGODB
     */
    MONGODB("Mongodb"),

    /**
     * PRESTO
     */
    PRESTO("Presto"),

    /**
     * HIVE
     */
    HIVE("Hive"),


    /**
     * REDIS
     */
    REDIS("Redis"),

    /**
     * KingBase
     */
    KINGBASE("KingBase");

    final String description;

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
        META_SCHEMA_MAP.put(MONGODB, new MongodbMetaSchemaSupport());
        META_SCHEMA_MAP.put(PRESTO, new PrestoMetaSchemaSupport());
        META_SCHEMA_MAP.put(REDIS, new RedisMetaSchemaSupport());
        META_SCHEMA_MAP.put(KINGBASE, new KingBaseSchemaSupport());
        META_SCHEMA_MAP.put(HIVE, new HiveMetaSchemaSupport());
    }

    DbTypeEnum(String description) {
        this.description = description;
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
            case PRESTO:
                SpiExample = SpiExample.builder().createTable("").alterTable("").build();
                break;
            default:
        }
        return SpiExample;
    }

}
