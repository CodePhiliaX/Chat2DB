package ai.chat2db.server.domain.api.enums;


import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * table vector status
 *
 * @author moji
 */
@Getter
public enum TableVectorEnum implements BaseEnum<String> {
    /**
     * SAVED
     */
    SAVED( "SAVED"),


    ;

    final String description;


    TableVectorEnum(String description) {
        this.description = description;
    }

    /**
     * 通过名称获取枚举
     *
     * @param name
     * @return
     */
    public static TableVectorEnum getByName(String name) {
        for (TableVectorEnum dbTypeEnum : TableVectorEnum.values()) {
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
