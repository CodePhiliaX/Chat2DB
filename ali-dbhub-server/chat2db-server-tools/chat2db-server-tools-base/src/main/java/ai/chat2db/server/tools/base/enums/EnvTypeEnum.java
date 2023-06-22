package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * @author moji
 * @version ConnectionTypeEnum.java, v 0.1 2022年09月16日 14:59 moji Exp $
 * @date 2022/09/16
 */
@Getter
public enum EnvTypeEnum implements BaseEnum<String> {

    /**
     * 日常环境
     */
    DAILY("日常环境"),

    /**
     * 生产环境
     */
    PRODUCT("生产环境"),

    ;

    final String description;

    EnvTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
