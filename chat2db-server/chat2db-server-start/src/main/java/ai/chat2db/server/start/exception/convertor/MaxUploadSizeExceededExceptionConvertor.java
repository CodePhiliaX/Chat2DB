package ai.chat2db.server.start.exception.convertor;

import ai.chat2db.server.tools.base.excption.CommonErrorEnum;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

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
