package ai.chat2db.server.tools.base.enums;

import lombok.Getter;

/**
 * @author: zgq
 * @date: 2024年05月01日 19:57
 */
@Getter
public enum JdbcUrlParameterEnum implements BaseEnum<String> {

    CONTINUE_BATCH_ON_ERROR("continueBatchOnError");

     JdbcUrlParameterEnum(String description) {
        this.description = description;
    }

    final String description;

    /**
     * @return
     */
    @Override
    public String getCode() {
        return this.name();
    }
}
