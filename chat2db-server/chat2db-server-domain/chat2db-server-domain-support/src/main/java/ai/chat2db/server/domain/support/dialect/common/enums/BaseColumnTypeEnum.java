package ai.chat2db.server.domain.support.dialect.common.enums;

import ai.chat2db.server.domain.support.enums.ColumnTypeEnum;
import ai.chat2db.server.tools.base.enums.BaseEnum;

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
