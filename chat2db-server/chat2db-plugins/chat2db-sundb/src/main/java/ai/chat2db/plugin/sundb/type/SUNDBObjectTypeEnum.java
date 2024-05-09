package ai.chat2db.plugin.sundb.type;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum SUNDBObjectTypeEnum {

    FUNCTION("FUNCTION"),
    PROCEDURE("PROCEDURE"),
    VIEW("VIEW"),
    ;

    private String objectType;
    SUNDBObjectTypeEnum(String objectType) {
        this.objectType = objectType;
    }
}
