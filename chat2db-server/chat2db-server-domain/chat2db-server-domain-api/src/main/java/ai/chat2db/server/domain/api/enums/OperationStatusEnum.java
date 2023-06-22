package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * 状态
 *
 * @author 是仪
 */
@Getter
public enum OperationStatusEnum implements BaseEnum<String> {
    /**
     * 草稿
     */
    DRAFT("草稿"),

    /**
     * 已发布
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
