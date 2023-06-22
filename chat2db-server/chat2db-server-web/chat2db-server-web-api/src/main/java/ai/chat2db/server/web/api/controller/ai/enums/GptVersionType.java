package ai.chat2db.server.web.api.controller.ai.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * @author moji
 * @version GptModelType.java, v 0.1 2023年04月09日 19:05 moji Exp $
 * @date 2023/04/09
 */
@Getter
public enum GptVersionType implements BaseEnum {

    /**
     * GPT-3
     */
    GPT3("GPT-3"),

    /**
     * GPT-3-5
     */
    GPT35("GPT-3.5"),
    ;

    final String description;

    GptVersionType(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
