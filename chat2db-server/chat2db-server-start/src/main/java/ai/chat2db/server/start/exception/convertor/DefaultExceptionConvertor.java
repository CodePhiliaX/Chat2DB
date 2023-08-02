package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.util.ExceptionUtils;

/**
 * 默认的异常处理
 * 直接抛出系统异常
 *
 * @author 是仪
 */
public class DefaultExceptionConvertor implements ExceptionConvertor<Throwable> {

    @Override
    public ActionResult convert(Throwable exception) {
        return ActionResult.fail("common.systemError", I18nUtils.getMessage("common.systemError"), ExceptionUtils.getErrorInfoFromException(exception));
    }
}
