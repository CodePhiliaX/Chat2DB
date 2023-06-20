package com.alibaba.dbhub.server.start.exception.convertor;

import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;

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
