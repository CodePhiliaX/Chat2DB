package com.alibaba.dbhub.server.domain.support.dialect.h2;

import com.alibaba.dbhub.server.domain.support.dialect.common.enums.BaseColumnTypeEnum;
import com.alibaba.dbhub.server.domain.support.enums.ColumnTypeEnum;

import lombok.Getter;

/**
 * 列的类型
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum H2ColumnTypeEnum implements BaseColumnTypeEnum {
    /**
     * BIGINT
     */
    BIGINT("BIGINT", ColumnTypeEnum.BIGINT),
    /**
     * BIGINT
     */
    CHARACTER_VARYING("CHARACTER VARYING", ColumnTypeEnum.VARCHAR),
    /**
     * TIMESTAMP
     */
    TIMESTAMP("TIMESTAMP", ColumnTypeEnum.TIMESTAMP),
    /**
     * INTEGER
     */
    INTEGER("INTEGER", ColumnTypeEnum.INTEGER),
    ;

    final String code;
    final ColumnTypeEnum columnType;

    H2ColumnTypeEnum(String code, ColumnTypeEnum columnType) {
        this.code = code;
        this.columnType = columnType;
    }

    @Override
    public String getDescription() {
        return getCode();
    }

}
