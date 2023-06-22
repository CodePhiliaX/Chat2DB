package ai.chat2db.spi.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * 列的类型
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum ColumnTypeEnum implements BaseEnum<String> {
    /**
     * BIGINT
     */
    BIGINT,

    /**
     * VARCHAR
     */
    VARCHAR,

    /**
     * TIMESTAMP
     */
    TIMESTAMP,

    /**
     * DATETIME
     */
    DATETIME,

    /**
     * INTEGER
     */
    INTEGER,

    ;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.name();
    }
}
