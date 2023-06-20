package com.alibaba.dbhub.server.start.exception.convertor;

import com.alibaba.dbhub.server.tools.base.excption.CommonErrorEnum;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;

import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * MaxUploadSizeExceededException
 *
 * @author 是仪
 */
public class MaxUploadSizeExceededExceptionConvertor implements ExceptionConvertor<MaxUploadSizeExceededException> {

    @Override
    public ActionResult convert(MaxUploadSizeExceededException exception) {
        return ActionResult.fail(CommonErrorEnum.MAX_UPLOAD_SIZE);
    }
}
