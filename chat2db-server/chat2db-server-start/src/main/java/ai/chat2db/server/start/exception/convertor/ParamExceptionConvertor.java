package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.excption.CommonErrorEnum;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
/**
 * 参数异常 目前包括
 * ConstraintViolationException
 * MissingServletRequestParameterException
 * IllegalArgumentException
 *
 * @author 是仪
 */
public class ParamExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail(CommonErrorEnum.PARAM_ERROR, exception.getMessage());
    }
}
