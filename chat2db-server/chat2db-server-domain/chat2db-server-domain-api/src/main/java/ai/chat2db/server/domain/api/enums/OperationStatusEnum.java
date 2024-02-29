package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * state
 *
 * @author Shi Yi
 */
@Getter
public enum OperationStatusEnum implements BaseEnum<String> {
    /**
     * draft
     */
    DRAFT("草稿"),

    /**
     * Published
     */
    RELEASE("已发布"),

    ;

    final String description;

    OperationStatusEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
