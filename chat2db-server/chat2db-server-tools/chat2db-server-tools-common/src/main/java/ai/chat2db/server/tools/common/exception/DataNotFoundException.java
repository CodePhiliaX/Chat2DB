package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * Data not found exceptions
 *
 * @author Jiaju Zhuang
 */
@Getter
public class DataNotFoundException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public DataNotFoundException() {
        super("common.dataNotFound");
    }

}