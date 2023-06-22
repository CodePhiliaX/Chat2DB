package ai.chat2db.spi.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * 驱动类枚举
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum CellTypeEnum implements BaseEnum<String> {
    /**
     * 字符串
     */
    STRING("字符串"),

    /**
     * 数字
     */
    BIG_DECIMAL("数字"),

    /**
     * 日期
     */
    DATE("日期"),

    /**
     * 二进制流
     */
    BYTE("二进制流"),

    /**
     * 空数据
     */
    EMPTY("空数据"),
    ;

    final String description;

    CellTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
