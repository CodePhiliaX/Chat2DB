package com.alibaba.dbhub.server.tools.common.exception;

import com.alibaba.dbhub.server.tools.base.excption.BusinessException;
import com.alibaba.dbhub.server.tools.common.enums.ErrorEnum;

import lombok.Getter;

/**
 * 需要重定向的业务异常
 *
 * @author Jiaju Zhuang
 */
@Getter
public class RedirectBusinessException extends BusinessException {

    private static final long serialVersionUID = -7370118120765115377L;
    private final String redirect;

    public RedirectBusinessException(String redirect) {
        super(ErrorEnum.REDIRECT);
        this.redirect = redirect;
    }
}