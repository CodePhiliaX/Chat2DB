package ai.chat2db.plugin.mysql.type;

import ai.chat2db.spi.model.ColumnType;

public enum MysqlColumnTypeEnum {

    BIT("BIT", true, false, true, false, false, false, true, true, false),

    TINYINT("TINYINT", false, false, true, true, false, false, true, true, false),

    TINYINT_UNSIGNED("TINYINT UNSIGNED", false, false, true, true, false, false, true, true, false),

    SMALLINT("SMALLINT", false, false, true, true, false, false, true, true, false),

    SMALLINT_UNSIGNED("SMALLINT UNSIGNED", false, false, true, true, false, false, true, true, false),

    MEDIUMINT("MEDIUMINT", false, false, true, true, false, false, true, true, false),

    MEDIUMINT_UNSIGNED("MEDIUMINT UNSIGNED", false, false, true, true, false, false, true, true, false),

    INT("INT", false, false, true, true, false, false, true, true, false),


    INT_UNSIGNED("INT UNSIGNED", false, false, true, true, false, false, true, true, false),

    BIGINT("BIGINT", false, false, true, true, false, false, true, true, false),


    BIGINT_UNSIGNED("BIGINT UNSIGNED", false, false, true, true, false, false, true, true, false),


    DECIMAL("DECIMAL", true, true, true, false, false, false, true, true, false),

    DECIMAL_UNSIGNED("DECIMAL UNSIGNED", true, true, true, false, false, false, true, true, false),


    FLOAT("FLOAT", true, true, true, false, false, false, true, true, false), FLOAT_UNSIGNED("FLOAT UNSIGNED", true, true, true, false, false, false, true, true, false),

    DOUBLE("DOUBLE", true, true, true, false, false, false, true, true, false),

    DOUBLE_UNSIGNED("DOUBLE UNSIGNED", true, true, true, false, false, false, true, true, false), DATE("DATE", false, false, true, false, false, false, true, true, false), DATETIME("DATETIME", true, false, true, false, false, false, true, true, true), TIMESTAMP("TIMESTAMP", true, false, true, false, false, false, true, true, true), TIME("TIME", true, false, true, false, false, false, true, true, false), YEAR("YEAR", false, false, true, false, false, false, true, true, false), CHAR("CHAR", true, false, true, false, false, true, true, true, false), VARCHAR("VARCHAR", true, false, true, false, false, true, true, true, false), BINARY("BINARY", true, false, true, false, false, false, true, true, false), VARBINARY("VARBINARY", true, false, true, false, false, false, true, true, false), TINYBLOB("TINYBLOB", false, false, true, false, false, false, true, false, false), BLOB("BLOB", false, false, true, false, false, false, true, false, false), MEDIUMBLOB("MEDIUMBLOB", false, false, true, false, false, false, true, false, false), LONGBLOB("LONGBLOB", false, false, true, false, false, false, true, false, false), TINYTEXT("TINYTEXT", false, false, true, false, false, true, true, false, false), TEXT("TEXT", false, false, true, false, false, true, true, false, false), MEDIUMTEXT("MEDIUMTEXT", false, false, true, false, false, true, true, false, false), LONGTEXT("LONGTEXT", false, false, true, false, false, true, true, false, false), ENUM("ENUM", false, false, true, false, false, true, true, true, true),


    BOOL("BOOL", false, false, true, true, false, false, true, true, false),

    INTEGER("INTEGER", false, false, true, true, false, false, true, true, false), INTEGER_UNSIGNED("INTEGER UNSIGNED", false, false, true, true, false, false, true, true, false),

    REAL("REAL", true, true, true, false, false, false, true, true, false),

    SET("SET", false, false, true, false, false, true, true, true, true),


    GEOMETRY("GEOMETRY", false, false, true, false, false, false, true, false, false), POINT("POINT", false, false, true, false, false, false, true, false, false), LINESTRING("LINESTRING", false, false, true, false, false, false, true, false, false), POLYGON("POLYGON", false, false, true, false, false, false, true, false, false), MULTIPOINT("MULTIPOINT", false, false, true, false, false, false, true, false, false), MULTILINESTRING("MULTILINESTRING", false, false, true, false, false, false, true, false, false), MULTIPOLYGON("MULTIPOLYGON", false, false, true, false, false, false, true, false, false), GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", false, false, true, false, false, false, true, false, false),

    JSON("JSON", false, false, true, false, false, false, true, false, false);

    private ColumnType columnType;


    MysqlColumnTypeEnum(String dataTypeName, boolean supportLength, boolean supportScale, boolean supportNullable, boolean supportAutoIncrement, boolean supportCharset, boolean supportCollation, boolean supportComments, boolean supportDefaultValue, boolean supportExtent) {
        this.columnType = new ColumnType(dataTypeName, supportLength, supportScale, supportNullable, supportAutoIncrement, supportCharset, supportCollation, supportComments, supportDefaultValue, supportExtent);
    }


}
