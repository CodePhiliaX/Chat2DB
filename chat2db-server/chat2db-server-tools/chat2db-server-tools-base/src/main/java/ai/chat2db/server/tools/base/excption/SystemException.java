package ai.chat2db.server.tools.base.excption;

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
