package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * Environment
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum EnvironmentStyleEnum implements BaseEnum<String> {
    /**
     * RELEASE
     */
    RELEASE("RELEASE"),

    /**
     * TEST
     */
    TEST("TEST"),

    ;
    final String description;

    EnvironmentStyleEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
