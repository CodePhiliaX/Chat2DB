package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.excption.CommonErrorEnum;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * MethodArgumentNotValidException
 *
 * @author 是仪
 */
public class MethodArgumentNotValidExceptionConvertor implements ExceptionConvertor<MethodArgumentNotValidException> {

    @Override
    public ActionResult convert(MethodArgumentNotValidException exception) {
        String message = ExceptionConvertorUtils.buildMessage(exception.getBindingResult());
        return ActionResult.fail(CommonErrorEnum.PARAM_ERROR, message);
    }
}
