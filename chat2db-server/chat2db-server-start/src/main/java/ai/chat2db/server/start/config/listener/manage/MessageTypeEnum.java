package ai.chat2db.server.start.config.listener.manage;

import ai.chat2db.server.tools.base.enums.BaseEnum;
import lombok.Getter;

/**
 * Message type enum
 *
 * @author Jiaju Zhuang
 */
@Getter
public enum MessageTypeEnum implements BaseEnum<String> {
    /**
     * Check if it works properly
     */
    HEARTBEAT,


    ;



    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getDescription() {
        return this.name();
    }
}
