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
    DESKTOP("DESKTOP", 1L),

    /**
     * ADMIN
     */
    ADMIN("ADMIN", 2L),

    /**
     * USER
     */
    USER("USER", null),

    ;
    final String description;
    final Long defaultUserId;

    RoleCodeEnum(String description, Long defaultUserId) {
        this.description = description;
        this.defaultUserId = defaultUserId;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
