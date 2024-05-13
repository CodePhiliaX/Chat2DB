package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * System environment
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum SystemEnvironmentEnum implements BaseEnum<String> {

    /**
     * dev
     */
    DEV("dev", "本地"),

    /**
     * test
     */
    TEST("test", "测试"),

    /**
     * release
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
