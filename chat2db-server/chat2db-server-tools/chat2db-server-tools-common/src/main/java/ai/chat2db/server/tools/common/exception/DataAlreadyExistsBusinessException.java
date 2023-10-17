package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * Data already exists exception
 *
 * @author Jiaju Zhuang
 */
@Getter
public class DataAlreadyExistsBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public DataAlreadyExistsBusinessException() {
        super("common.dataAlreadyExists");
    }

    public DataAlreadyExistsBusinessException(String key, Object value) {
        super("common.dataAlreadyExistsWithParam", new Object[] {key, value});
    }
}