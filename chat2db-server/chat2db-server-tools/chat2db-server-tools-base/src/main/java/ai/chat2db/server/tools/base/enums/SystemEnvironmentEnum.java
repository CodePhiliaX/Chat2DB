package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * 系统环境
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum SystemEnvironmentEnum implements BaseEnum<String> {

    /**
     * 本地
     */
    DEV("dev", "本地"),

    /**
     * 测试
     */
    TEST("test", "测试"),

    /**
     * 正式
     */
    RELEASE("release", "正式"),

    ;

    final String code;

    final String description;

    SystemEnvironmentEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
