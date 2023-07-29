package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * Parameter exceptions
 *
 * @author Jiaju Zhuang
 */
@Getter
public class ParamBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public ParamBusinessException() {
        super("common.paramError");
    }

    public ParamBusinessException(String paramString) {
        super("common.paramDetailError", new Object[] {paramString});
    }
}