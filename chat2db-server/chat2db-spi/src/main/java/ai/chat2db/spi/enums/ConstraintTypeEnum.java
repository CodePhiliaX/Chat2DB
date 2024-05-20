package ai.chat2db.spi.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum ConstraintTypeEnum implements BaseEnum<String> {
    PRIMARY_KEY("PRIMARY KEY"),
    FOREIGN_KEY("FOREIGN KEY"),
    UNIQUE("UNIQUE"),
    CHECK("CHECK"),
    ;

    final String description;

    ConstraintTypeEnum(String description) {
        this.description = description;
    }


    @Override
    public String getCode() {
        return this.name();
    }
}
