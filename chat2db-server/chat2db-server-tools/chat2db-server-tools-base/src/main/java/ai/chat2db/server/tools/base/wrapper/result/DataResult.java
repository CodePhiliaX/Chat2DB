package ai.chat2db.server.tools.base.wrapper.result;

import java.io.Serializable;
import java.util.function.Function;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.wrapper.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * data的返回对象
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class DataResult<T> implements Serializable, Result<T> {
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
     * 数据信息
     */
    private T data;

    /**
     * traceId
     */
    private String traceId;

    public DataResult() {
        this.success = Boolean.TRUE;
    }

    private DataResult(T data) {
        this();
        this.data = data;
    }

    /**
     * 构建返回对象
     *
     * @param data 需要构建的对象
     * @param <T>  需要构建的对象类型
     * @return 返回的结果
     */
    public static <T> DataResult<T> of(T data) {
        return new DataResult<>(data);
    }

    /**
     * 构建空的返回对象
     *
     * @param <T> 需要构建的对象类型
     * @return 返回的结果
     */
    public static <T> DataResult<T> empty() {
        return new DataResult<>();
    }

    /**
     * 构建异常返回
     *
     * @param errorCode    错误编码
     * @param errorMessage 错误信息
     * @param <T>          需要构建的对象类型
     * @return 返回的结果
     */
    public static <T> DataResult<T> error(String errorCode, String errorMessage) {
        DataResult<T> result = new DataResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = false;
        return result;
    }


    /**
     * 判断是否存在数据
     *
     * @param dataResult
     * @return 是否存在数据
     */
    public static boolean hasData(DataResult<?> dataResult) {
        return dataResult != null && dataResult.getSuccess() && dataResult.getData() != null;
    }

    /**
     * 将当前的类型转换成另外一个类型
     *
     * @param mapper 转换的方法
     * @param <R>    返回的类型
     * @return 返回的结果
     */
    public <R> DataResult<R> map(Function<T, R> mapper) {
        R returnData = hasData(this) ? mapper.apply(getData()) : null;
        DataResult<R> dataResult = new DataResult<>();
        dataResult.setSuccess(getSuccess());
        dataResult.setErrorCode(getErrorCode());
        dataResult.setErrorMessage(getErrorMessage());
        dataResult.setData(returnData);
        dataResult.setTraceId(getTraceId());
        return dataResult;
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
}
