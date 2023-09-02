package ai.chat2db.server.tools.base.wrapper.result;

import java.io.Serial;
import java.io.Serializable;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.wrapper.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * action的返回对象
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class ActionResult implements Serializable, Result {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * 是否成功
     *
     * @mock true
     */
    private Boolean success;

    /**
     * 错误编码
     *
     * @see CommonErrorEnum
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * error detail
     */
    private String errorDetail;

    /**
     * solution link
     */
    private String solutionLink;

    /**
     * traceId
     */
    private String traceId;

    public ActionResult() {
        this.success = Boolean.TRUE;
    }

    /**
     * 返回成功
     *
     * @return 运行结果
     */
    public static ActionResult isSuccess() {
        return new ActionResult();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public void success(boolean success) {
        this.success = success;
    }

    @Override
    public String errorCode() {
        return errorCode;
    }

    @Override
    public void errorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public void errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void errorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    @Override
    public String errorDetail() {
        return errorDetail;
    }

    @Override
    public void solutionLink(String solutionLink) {
        this.solutionLink = solutionLink;
    }

    @Override
    public String solutionLink() {
        return solutionLink;
    }

    /**
     * 返回失败
     *
     * @param errorCode    error code
     * @param errorMessage error message
     * @param errorDetail  error detail
     * @return 运行结果
     */
    public static ActionResult fail(String errorCode, String errorMessage, String errorDetail) {
        ActionResult result = new ActionResult();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.FALSE;
        result.solutionLink("https://github.com/chat2db/Chat2DB/wiki/Chat2DB");
        result.errorDetail(errorDetail);
        return result;
    }

    public DataResult<Boolean> toBooleaSuccessnDataResult() {
        return DataResult.<Boolean>builder()
            .success(success)
            .errorCode(errorCode)
            .errorMessage(errorMessage)
            .errorDetail(errorDetail)
            .solutionLink(solutionLink)
            .traceId(traceId)
            .data(Boolean.TRUE)
            .build();
    }

}
