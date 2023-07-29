package ai.chat2db.server.domain.api.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * How much data is currently needed at the beginning
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum ExportSizeEnum implements BaseEnum<String> {
    /**
     * CURRENT_PAGE
     */
    CURRENT_PAGE("CURRENT_PAGE"),

    /**
     * ALL
     */
    ALL("ALL"),

    ;

    final String description;

    ExportSizeEnum(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }

}
