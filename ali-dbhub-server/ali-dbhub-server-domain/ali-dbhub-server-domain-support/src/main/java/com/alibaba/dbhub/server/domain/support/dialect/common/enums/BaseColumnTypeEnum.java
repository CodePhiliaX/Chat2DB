package com.alibaba.dbhub.server.domain.support.dialect.common.enums;

import com.alibaba.dbhub.server.domain.support.enums.ColumnTypeEnum;
import com.alibaba.dbhub.server.tools.base.enums.BaseEnum;

/**
 * 列的类型
 *
 * @author Jiaju Zhuang
 */
public interface BaseColumnTypeEnum extends BaseEnum<String> {

    /**
     * 返回列的类型
     *
     * @return
     */
    ColumnTypeEnum getColumnType();
}
