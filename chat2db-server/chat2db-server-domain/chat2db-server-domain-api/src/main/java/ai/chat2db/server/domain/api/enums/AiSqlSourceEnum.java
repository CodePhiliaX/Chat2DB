package ai.chat2db.server.domain.api.enums;


import ai.chat2db.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * AI SQL选择的AI模型类型
 *
 * @author moji
 */
@Getter
public enum AiSqlSourceEnum implements BaseEnum<String> {
    /**
     * OPENAI
     */
    OPENAI( "OPENAI"),

    /**
     * RESTAI
     */
    RESTAI("RESTAI"),

    /**
     * AZURE OPENAI
     */
    AZUREAI("AZURE OPENAI"),

    /**
     * CHAT2DB OPENAI
     */
    CHAT2DBAI("CHAT2DB OPENAI"),

    /**
     * CLAUDE AI
     */
    CLAUDEAI("CLAUDE AI"),

    /**
     * WNEXIN AI
     */
    WENXINAI("WENXIN AI"),

    /**
     * BAICHUAN AI
     */
    BAICHUANAI("BAICHUAN AI"),

    /**
     * ZHIPU AI
     */
    ZHIPUAI("ZHIPU AI"),

    /**
     * TONGYIQIANWEN AI
     */
    TONGYIQIANWENAI("TONGYIQIANWEN AI"),

    /**
     * FAST CHAT AI
     */
    FASTCHATAI("FAST CHAT AI"),

    ;

    final String description;


    AiSqlSourceEnum(String description) {
        this.description = description;
    }

    /**
     * 通过名称获取枚举
     *
     * @param name
     * @return
     */
    public static AiSqlSourceEnum getByName(String name) {
        for (AiSqlSourceEnum dbTypeEnum : AiSqlSourceEnum.values()) {
            if (dbTypeEnum.name().equals(name)) {
                return dbTypeEnum;
            }
        }
        return null;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
