package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * Access Object Type
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum AccessObjectTypeEnum implements BaseEnum<String> {
    /**
     * TEAM
     */
    TEAM("TEAM"),

    /**
     * USER
     */
    USER("USER"),

    ;

    final String description;

    AccessObjectTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
