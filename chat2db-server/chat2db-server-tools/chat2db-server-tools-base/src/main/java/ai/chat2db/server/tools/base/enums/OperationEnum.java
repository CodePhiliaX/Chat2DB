package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * Operation enumeration
 *
 * @author Shi Yi
 */
@Getter
public enum OperationEnum implements BaseEnum<String> {
    /**
     * creat
     */
    CREATE("creat"),

    /**
     * update
     */
    UPDATE("update"),

    /**
     * delete
     */
    DELETE("delete"),

    ;

    final String description;

    OperationEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
