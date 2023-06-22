package ai.chat2db.server.domain.support.dialect.h2;

import ai.chat2db.server.domain.support.enums.IndexTypeEnum;
import ai.chat2db.server.domain.support.dialect.common.enums.BaseIndexTypeEnum;

import lombok.Getter;

/**
 * 列的类型
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum H2IndexTypeEnum implements BaseIndexTypeEnum {
    /**
     * PRIMARY_KEY
     */
    PRIMARY_KEY("PRIMARY KEY", IndexTypeEnum.PRIMARY_KEY),
    /**
     * UNIQUE INDEX
     */
    UNIQUE("UNIQUE INDEX", IndexTypeEnum.UNIQUE),
    /**
     * NORMAL
     */
    NORMAL("INDEX", IndexTypeEnum.NORMAL),
    ;

    final String code;
    final IndexTypeEnum indexType;

    H2IndexTypeEnum(String code, IndexTypeEnum indexType) {
        this.code = code;
        this.indexType = indexType;
    }

    @Override
    public String getDescription() {
        return getCode();
    }

}
