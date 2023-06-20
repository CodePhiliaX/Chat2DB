package com.alibaba.dbhub.server.domain.support.dialect.common.enums;

import com.alibaba.dbhub.server.domain.support.enums.CollationEnum;
import com.alibaba.dbhub.server.tools.base.enums.BaseEnum;

/**
 * 排序类型
 *
 * @author Jiaju Zhuang
 */
public interface BaseCollationEnum extends BaseEnum<String> {

    /**
     * 返回排序类型
     *
     * @return
     */
    CollationEnum getCollation();
}
