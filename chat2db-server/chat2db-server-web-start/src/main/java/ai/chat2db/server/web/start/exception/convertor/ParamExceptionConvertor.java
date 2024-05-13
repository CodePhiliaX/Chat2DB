package ai.chat2db.server.web.start.exception.convertor;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.spi.util.ExceptionUtils;

/**
 * Parameter exceptions currently include：
 * ConstraintViolationException
 * MissingServletRequestParameterException
 * IllegalArgumentException
 *
 * @author Shi Yi
 */
public class ParamExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail("common.paramError", exception.getMessage(), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
