package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * role code
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum RoleCodeEnum implements BaseEnum<String> {
    /**
     * DESKTOP
     */
    DESKTOP("DESKTOP"),

    /**
     * USER
     */
    USER("USER"),

    /**
     * ADMIN
     */
    ADMIN("ADMIN"),

    ;
    final String description;

    RoleCodeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
