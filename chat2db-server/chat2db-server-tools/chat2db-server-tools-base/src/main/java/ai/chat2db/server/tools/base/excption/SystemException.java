package ai.chat2db.server.tools.base.excption;

import lombok.Data;

/**
 * Business abnormality.
 * Simply put, exceptions that require manual intervention are called system exceptions.
 *
 * @author zhuangjiaju
 * @date 2021/06/26
 */
@Data
public class SystemException extends RuntimeException {

    /**
     * The encoding of the exception
     */
    private String code;
    /**
     * Exception information parameters
     */
    private Object[] args;

    public SystemException() {
        this("common.systemError");
    }

    public SystemException(String code) {
        this(code, null);
    }

    public SystemException(String code, Object[] args) {
        super(code);
        this.code = code;
        this.args = args;
    }

    public SystemException(String code, Object[] args, Throwable throwable) {
        super(code, throwable);
        this.code = code;
        this.args = args;
    }
}
