package ai.chat2db.server.tools.base.excption;


import ai.chat2db.server.tools.base.enums.BaseErrorEnum;

import lombok.Data;

/**
 * 业务异常。简单的说就是需要人工介入的异常叫做系统异常。
 *
 * @author zhuangjiaju
 * @date 2021/06/26
 */
@Data
public class SystemException extends RuntimeException {

    /**
     * 异常的编码
     */
    private String code;

    public SystemException(String message) {
        this(CommonErrorEnum.COMMON_SYSTEM_ERROR, message);
    }

    public SystemException(String message, Throwable throwable) {
        this(CommonErrorEnum.COMMON_SYSTEM_ERROR, message, throwable);
    }

    public SystemException(String code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(BaseErrorEnum errorEnum, String message, Throwable throwable) {
        super(message, throwable);
        this.code = errorEnum.getCode();
    }

    public SystemException(BaseErrorEnum errorEnum) {
        this(errorEnum.getCode(), errorEnum.getDescription());
    }

    public SystemException(BaseErrorEnum errorEnum, String message) {
        this(errorEnum.getCode(), message);
    }

    public SystemException(BaseErrorEnum errorEnum, Throwable throwable) {
        super(errorEnum.getDescription(), throwable);
        this.code = errorEnum.getCode();
    }
}
