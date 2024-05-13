package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.excption.BusinessException;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.util.ExceptionUtils;

/**
 * BusinessException
 *
 * @author Shi Yi
 */
public class BusinessExceptionConvertor implements ExceptionConvertor<BusinessException> {

    @Override
    public ActionResult convert(BusinessException exception) {
        return ActionResult.fail(exception.getCode(), I18nUtils.getMessage(exception.getCode(), exception.getArgs()),
            ExceptionUtils.getErrorInfoFromException(exception));
    }
}
