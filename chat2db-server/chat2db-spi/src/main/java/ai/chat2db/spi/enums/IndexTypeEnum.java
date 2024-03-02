package ai.chat2db.spi.enums;


import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * Index type
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum IndexTypeEnum implements BaseEnum<String> {
    /**
     * primary key
     */
    PRIMARY_KEY("primary key"),

    /**
     * Ordinary index
     */
    NORMAL("Ordinary index"),

    /**
     * unique index
     */
    UNIQUE("unique index"),
    ;

    final String description;

    IndexTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
