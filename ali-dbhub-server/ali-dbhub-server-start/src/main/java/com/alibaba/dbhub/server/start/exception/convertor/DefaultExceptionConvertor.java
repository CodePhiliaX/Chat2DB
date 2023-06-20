package com.alibaba.dbhub.server.start.exception.convertor;

import com.alibaba.dbhub.server.tools.base.excption.CommonErrorEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;

/**
 * 默认的异常处理
 * 直接抛出系统异常
 *
 * @author 是仪
 */
public class DefaultExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail(CommonErrorEnum.COMMON_SYSTEM_ERROR);
    }
}
