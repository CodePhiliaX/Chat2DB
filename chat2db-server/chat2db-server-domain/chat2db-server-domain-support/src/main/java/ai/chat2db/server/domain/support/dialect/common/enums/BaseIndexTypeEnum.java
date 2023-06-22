package ai.chat2db.server.domain.support.dialect.common.enums;

import ai.chat2db.server.domain.support.enums.IndexTypeEnum;
import ai.chat2db.server.tools.base.enums.BaseEnum;

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
