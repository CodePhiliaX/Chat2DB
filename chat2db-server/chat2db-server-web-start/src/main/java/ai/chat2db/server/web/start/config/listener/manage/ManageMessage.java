package ai.chat2db.server.web.start.config.listener.manage;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * Administrative messages
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ManageMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * Message type
     *
     * @see MessageTypeEnum
     */
    private MessageTypeEnum messageTypeEnum;
}
