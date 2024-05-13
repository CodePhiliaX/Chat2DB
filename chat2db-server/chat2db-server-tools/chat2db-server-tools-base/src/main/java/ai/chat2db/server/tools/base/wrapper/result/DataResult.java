package ai.chat2db.server.tools.base.wrapper.result;

import java.io.Serializable;
import java.util.function.Function;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.wrapper.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * data return object
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class DataResult<T> implements Serializable, Result<T> {
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * whether succeed
     *
     * @mock true
     */
    private Boolean success;

    /**
     * error coding
     *
     * @see CommonErrorEnum
     */
    private String errorCode;

    /**
     * error message
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
     * Data information
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
     * Construct the return object
     *
     * @param data object to be constructed
     * @param <T> The object type to be constructed
     * @return the returned result
     */
    public static <T> DataResult<T> of(T data) {
        return new DataResult<>(data);
    }

    /**
     * Construct an empty return object
     *
     * @param <T> The object type to be constructed
     * @return the returned result
     */
    public static <T> DataResult<T> empty() {
        return new DataResult<>();
    }

    /**
     * Build exception return
     *
     * @param errorCode error coding
     * @param errorMessage error message
     * @param <T> The object type to be constructed
     * @return the returned result
     */
    public static <T> DataResult<T> error(String errorCode, String errorMessage) {
        DataResult<T> result = new DataResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = false;
        return result;
    }


    /**
     * Determine whether data exists
     *
     * @param dataResult
     * @return whether data exists
     */
    public static boolean hasData(DataResult<?> dataResult) {
        return dataResult != null && dataResult.getSuccess() && dataResult.getData() != null;
    }

    /**
     * Convert the current type to another type
     *
     * @param mapper conversion method
     * @param <R> Return type
     * @return the returned result
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
