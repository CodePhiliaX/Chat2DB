package ai.chat2db.server.domain.support.dialect.common.enums;

import ai.chat2db.server.domain.support.enums.CollationEnum;
import ai.chat2db.server.tools.base.enums.BaseEnum;

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
