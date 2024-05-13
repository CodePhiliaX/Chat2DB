package ai.chat2db.server.tools.base.wrapper.result;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.wrapper.Result;

import jakarta.validation.constraints.NotNull;
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
public class ListResult<T> implements Serializable, Result<T> {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * whether succeed
     *
     * @mock true
     */
    private Boolean success;

    /**
     * error coding
     */
    private String errorCode;
    /**
     * Exception information
     */
    private String errorMessage;
    /**
     * Data information
     */
    private List<T> data;
    /**
     * traceId
     */
    private String traceId;

    /**
     * error detail
     */
    private String errorDetail;

    /**
     * solution link
     */
    private String solutionLink;

    public ListResult() {
        this.success = Boolean.TRUE;
    }

    private ListResult(List<T> data) {
        this();
        this.data = data;
    }

    /**
     * Build the list and return the object
     *
     * @param data object to be constructed
     * @param <T> The object type to be constructed
     * @return the returned list
     */
    public static <T> ListResult<T> of(List<T> data) {
        return new ListResult<>(data);
    }

    /**
     * Build an empty list and return the object
     *
     * @param <T> The type of object to be constructed
     * @return the returned list
     */
    public static <T> ListResult<T> empty() {
        return of(Collections.emptyList());
    }

    /**
     * Build exception return list
     *
     * @param errorCode error coding
     * @param errorMessage error message
     * @param <T> The object type to be constructed
     * @return the returned list
     */
    public static <T> ListResult<T> error(String errorCode, String errorMessage) {
        ListResult<T> result = new ListResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.TRUE;
        return result;
    }

    /**
     * Determine whether data exists
     *
     * @param listResult
     * @return whether data exists
     */
    public static boolean hasData(ListResult<?> listResult) {
        return listResult != null && listResult.getSuccess() && listResult.getData() != null && !listResult.getData()
            .isEmpty();
    }

    /**
     * Convert the current type to another type
     *
     * @param mapper conversion method
     * @param <R> Return type
     * @return paging return object
     */
    public <R> ListResult<R> map(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().stream().map(mapper).collect(Collectors.toList())
            : Collections.emptyList();
        ListResult<R> listResult = new ListResult<>();
        listResult.setSuccess(getSuccess());
        listResult.setErrorCode(getErrorCode());
        listResult.setErrorMessage(getErrorMessage());
        listResult.setData(returnData);
        listResult.setTraceId(getTraceId());
        return listResult;
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
