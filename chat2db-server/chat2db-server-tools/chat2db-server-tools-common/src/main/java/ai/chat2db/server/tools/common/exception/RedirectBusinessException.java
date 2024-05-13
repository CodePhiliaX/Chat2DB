package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.excption.BusinessException;
import lombok.Getter;

/**
 * Business exceptions that require redirection
 *
 * @author Jiaju Zhuang
 */
@Getter
public class RedirectBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    private final String redirect;

    public RedirectBusinessException(String redirect) {
        super("common.redirect");
        this.redirect = redirect;
    }
}