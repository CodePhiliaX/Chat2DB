package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

/**
 * 异常转换器
 *
 * @author 是仪
 */
public interface ExceptionConvertor<T extends Throwable> {

    /**
     * 转换异常
     *
     * @param exception
     * @return
     */
    ActionResult convert(T exception);
}
