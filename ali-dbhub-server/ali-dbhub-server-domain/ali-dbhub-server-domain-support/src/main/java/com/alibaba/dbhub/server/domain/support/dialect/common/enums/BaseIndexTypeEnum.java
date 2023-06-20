package com.alibaba.dbhub.server.domain.support.dialect.common.enums;

import com.alibaba.dbhub.server.domain.support.enums.IndexTypeEnum;
import com.alibaba.dbhub.server.tools.base.enums.BaseEnum;

/**
 * 索引的类型
 *
 * @author Jiaju Zhuang
 */
public interface BaseIndexTypeEnum extends BaseEnum<String> {

    /**
     * 返回索引的类型
     *
     * @return
     */
    IndexTypeEnum getIndexType();
}
