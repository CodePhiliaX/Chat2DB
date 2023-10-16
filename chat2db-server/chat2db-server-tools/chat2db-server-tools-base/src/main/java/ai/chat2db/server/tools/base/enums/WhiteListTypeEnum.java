package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * @author moji
 * @version WhiteListTypeEnum.java, v 0.1 2022年09月25日 16:57 moji Exp $
 * @date 2022/09/25
 */
@Getter
public enum WhiteListTypeEnum implements BaseEnum<String> {

    /**
     * 向量接口
     */
    VECTOR("VECTOR"),

    ;

    final String description;

    WhiteListTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
