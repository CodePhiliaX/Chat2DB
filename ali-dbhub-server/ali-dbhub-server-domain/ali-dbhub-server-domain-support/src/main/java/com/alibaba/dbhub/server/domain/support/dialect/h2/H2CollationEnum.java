package com.alibaba.dbhub.server.domain.support.dialect.h2;

import com.alibaba.dbhub.server.domain.support.enums.CollationEnum;
import com.alibaba.dbhub.server.domain.support.dialect.common.enums.BaseCollationEnum;

import lombok.Getter;

/**
 * 排序枚举
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum H2CollationEnum implements BaseCollationEnum {
    /**
     * ASC
     */
    ASC("ASC", CollationEnum.ASC),
    /**
     * DESC
     */
    DESC("DESC", CollationEnum.DESC),
    ;

    final String code;
    final CollationEnum collation;

    H2CollationEnum(String code, CollationEnum collation) {
        this.code = code;
        this.collation = collation;
    }

    @Override
    public String getDescription() {
        return getCode();
    }

}
