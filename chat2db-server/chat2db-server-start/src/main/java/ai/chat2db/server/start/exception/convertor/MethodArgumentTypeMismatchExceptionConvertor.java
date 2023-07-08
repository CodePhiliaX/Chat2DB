package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.util.ExceptionUtils;
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
        return ActionResult.fail("common.paramError", I18nUtils.getMessage("common.paramError"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
