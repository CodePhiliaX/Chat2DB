package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * 是否枚举
 *
 * @author 是仪
 */
@Getter
public enum YesOrNoEnum implements BaseEnum<String> {

    /**
     * 是
     */
    YES("Y", "是", true),
    /**
     * 未读
     */
    NO("N", "否", false),

    ;

    final String letter;
    final String description;
    final boolean booleanValue;

    YesOrNoEnum(String letter, String description, boolean booleanValue) {
        this.letter = letter;
        this.description = description;
        this.booleanValue = booleanValue;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    /**
     * 根据布尔值转换
     *
     * @param booleanValue 布尔值
     * @return
     */
    public static YesOrNoEnum valueOf(Boolean booleanValue) {
        if (booleanValue == null) {
            return null;
        }
        if (booleanValue) {
            return YesOrNoEnum.YES;
        }
        return YesOrNoEnum.NO;
    }

}
