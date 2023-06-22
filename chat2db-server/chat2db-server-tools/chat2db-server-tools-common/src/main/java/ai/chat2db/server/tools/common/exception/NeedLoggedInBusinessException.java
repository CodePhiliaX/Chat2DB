package ai.chat2db.server.tools.common.exception;

import java.io.Serial;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.common.enums.ErrorEnum;

import lombok.Getter;

/**
 * 用户登录异常
 *
 * @author Jiaju Zhuang
 */
@Getter
public class NeedLoggedInBusinessException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 4481325884589199804L;

    public NeedLoggedInBusinessException() {
        super(ErrorEnum.NEED_LOGGED_IN);
    }
}