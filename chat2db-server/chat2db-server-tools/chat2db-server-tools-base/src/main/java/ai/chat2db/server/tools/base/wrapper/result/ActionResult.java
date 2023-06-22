package ai.chat2db.server.tools.base.wrapper.result;

import java.io.Serializable;


import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.enums.BaseErrorEnum;
import ai.chat2db.server.tools.base.excption.CommonErrorEnum;
import ai.chat2db.server.tools.base.wrapper.Result;

import jakarta.validation.constraints.NotNull;
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
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * 是否成功
     *
     * @mock true
     */
    @NotNull
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

    /**
     * 返回失败
     *
     * @param errorCode    错误编码
     * @param errorMessage 错误信息
     * @return 运行结果
     */
    public static ActionResult fail(String errorCode, String errorMessage) {
        ActionResult result = new ActionResult();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.FALSE;
        return result;
    }

    /**
     * 返回失败
     *
     * @param errorEnum 错误枚举
     * @return 运行结果
     */
    public static ActionResult fail(BaseErrorEnum errorEnum) {
        return fail(errorEnum.getCode(), errorEnum.getDescription());
    }

    /**
     * 返回失败
     *
     * @param errorEnum 错误枚举
     * @return 运行结果
     */
    public static ActionResult fail(BaseErrorEnum errorEnum, String errorMessage) {
        return fail(errorEnum.getCode(), errorMessage);
    }

}
