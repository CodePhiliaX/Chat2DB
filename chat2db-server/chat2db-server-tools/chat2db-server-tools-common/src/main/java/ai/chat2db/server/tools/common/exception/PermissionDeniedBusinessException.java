package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * Permission Denied
 *
 * @author Jiaju Zhuang
 */
@Getter
public class PermissionDeniedBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    public PermissionDeniedBusinessException() {
        super("common.permissionDenied");
    }
}