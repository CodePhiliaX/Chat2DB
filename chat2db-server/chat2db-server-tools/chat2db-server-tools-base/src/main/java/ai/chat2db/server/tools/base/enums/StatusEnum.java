package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * @author moji
 * @version StatusEnum.java, v 0.1 2022年09月25日 16:57 moji Exp $
 * @date 2022/09/25
 */
@Getter
public enum StatusEnum implements BaseEnum<String> {

    /**
     * 草稿
     */
    DRAFT("草稿"),

    /**
     * 发布
     */
    RELEASE("发布"),

    ;

    final String description;

    StatusEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
