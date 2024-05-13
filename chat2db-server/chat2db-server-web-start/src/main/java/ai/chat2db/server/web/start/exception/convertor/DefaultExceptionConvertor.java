package ai.chat2db.server.web.start.exception.convertor;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.util.ExceptionUtils;

/**
 * Default exception handling
 * Throw system exception directly
 *
 * @author Shi Yi
 */
public class DefaultExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail("common.systemError", I18nUtils.getMessage("common.systemError"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
