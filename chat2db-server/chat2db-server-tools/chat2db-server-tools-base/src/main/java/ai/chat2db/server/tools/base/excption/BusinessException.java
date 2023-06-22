package ai.chat2db.server.tools.base.excption;

import lombok.Data;

/**
 * 业务异常。不需要人工介入的叫做业务异常。
 *
 * @author zhuangjiaju
 * @date 2021/06/26
 */
@Data
public class BusinessException extends RuntimeException {
    /**
     * The encoding of the exception
     */
    private String code;
    /**
     * Exception information parameters
     */
    private Object[] args;

    public BusinessException() {
        this("common.businessError");
    }

    public BusinessException(String code) {
        this(code, null);
    }

    public BusinessException(String code, Object[] args) {
        super(code);
        this.code = code;
        this.args = args;
    }

    public BusinessException(String code, Object[] args, Throwable throwable) {
        super(code, throwable);
        this.code = code;
        this.args = args;
    }

}
