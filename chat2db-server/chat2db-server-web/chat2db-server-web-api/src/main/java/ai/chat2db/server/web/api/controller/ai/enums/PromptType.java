package ai.chat2db.server.web.api.controller.ai.enums;

import ai.chat2db.server.tools.base.enums.BaseEnum;

import lombok.Getter;

/**
 * prompt type
 *
 * @author moji
 * @version PromptType.java, v 0.1 April 9, 2023 15:36 moji Exp $
 * @date 2023/04/09
 */
@Getter
public enum PromptType implements BaseEnum<String> {

    /**
     * Convert natural language to SQL
     */
    NL_2_SQL("Convert natural language into SQL queries"),

    /**
     * Interpret SQL
     */
    SQL_EXPLAIN("Interpret SQL"),

    /**
     * SQL optimization
     */
    SQL_OPTIMIZER("Provide optimization suggestions"),

    /**
     * SQL conversion
     */
    SQL_2_SQL("Perform SQL conversion"),

    /**
     * text generation
     */
    TEXT_GENERATION("text generation"),
    ;

    final String description;

    PromptType(String description) {
        this.description = description;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
