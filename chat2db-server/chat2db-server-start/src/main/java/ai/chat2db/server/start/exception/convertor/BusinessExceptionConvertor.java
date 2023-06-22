package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

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
