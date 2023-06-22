package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * 用户登录异常
 *
 * @author Jiaju Zhuang
 */
@Getter
public class NeedLoggedInBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public NeedLoggedInBusinessException() {
        super("common.needLoggedIn");
    }
}