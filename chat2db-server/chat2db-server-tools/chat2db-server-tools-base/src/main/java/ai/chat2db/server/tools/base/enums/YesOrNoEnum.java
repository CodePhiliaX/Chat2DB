package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * Whether to enumerate
 *
 * @author Shi Yi
 */
@Getter
public enum YesOrNoEnum implements BaseEnum<String> {

    /**
     * yes
     */
    YES("Y", "是", true),
    /**
     * no
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
     * Convert based on boolean value
     *
     * @param booleanValue Boolean value
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
