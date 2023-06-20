package com.alibaba.dbhub.server.start.exception.convertor;

import com.alibaba.dbhub.server.tools.base.excption.CommonErrorEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * MethodArgumentTypeMismatchException
 *
 * @author 是仪
 */
public class MethodArgumentTypeMismatchExceptionConvertor
    implements ExceptionConvertor<MethodArgumentTypeMismatchException> {

    @Override
    public ActionResult convert(MethodArgumentTypeMismatchException exception) {
        return ActionResult.fail(CommonErrorEnum.PARAM_ERROR, "请输入正确的数据格式");
    }
}
