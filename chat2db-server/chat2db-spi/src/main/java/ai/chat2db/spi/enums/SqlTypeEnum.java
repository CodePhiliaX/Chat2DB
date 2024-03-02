package ai.chat2db.spi.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * sql type
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum SqlTypeEnum implements BaseEnum<String> {
    /**
     * Check for phrases
     */
    SELECT("Check for phrases"),

    /**
     * unknow
     */
    UNKNOWN("unknow"),

    ;

    final String description;

    SqlTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
