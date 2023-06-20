package com.alibaba.dbhub.server.start.exception.convertor;

import com.alibaba.dbhub.server.tools.base.excption.BusinessException;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;

/**
 * BusinessException
 *
 * @author 是仪
 */
public class BusinessExceptionConvertor implements ExceptionConvertor<BusinessException> {

    @Override
    public ActionResult convert(BusinessException exception) {
        return ActionResult.fail(exception.getCode(), exception.getMessage());
    }
}
