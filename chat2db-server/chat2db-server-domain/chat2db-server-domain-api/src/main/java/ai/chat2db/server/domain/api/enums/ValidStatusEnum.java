package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * Is it a valid enumeration
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum ValidStatusEnum implements BaseEnum<String> {
    /**
     * VALID
     */
    VALID("VALID"),

    /**
     * INVALID
     */
    INVALID("INVALID"),

    ;
    final String description;

    ValidStatusEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
