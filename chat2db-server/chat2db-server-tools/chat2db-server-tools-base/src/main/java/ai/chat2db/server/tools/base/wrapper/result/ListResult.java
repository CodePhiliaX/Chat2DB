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
 * data的返回对象
 *
 * @author 是仪
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class ListResult<T> implements Serializable, Result<T> {
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
     */
    private String errorCode;
    /**
     * 异常信息
     */
    private String errorMessage;
    /**
     * 数据信息
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
     * 构建列表返回对象
     *
     * @param data 需要构建的对象
     * @param <T>  需要构建的对象类型
     * @return 返回的列表
     */
    public static <T> ListResult<T> of(List<T> data) {
        return new ListResult<>(data);
    }

    /**
     * 构建空的列表返回对象
     *
     * @param <T> 需要构建的对象类型
     * @return 返回的列表
     */
    public static <T> ListResult<T> empty() {
        return of(Collections.emptyList());
    }

    /**
     * 构建异常返回列表
     *
     * @param errorCode    错误编码
     * @param errorMessage 错误信息
     * @param <T>          需要构建的对象类型
     * @return 返回的列表
     */
    public static <T> ListResult<T> error(String errorCode, String errorMessage) {
        ListResult<T> result = new ListResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.TRUE;
        return result;
    }

    /**
     * 判断是否存在数据
     *
     * @param listResult
     * @return 是否存在数据
     */
    public static boolean hasData(ListResult<?> listResult) {
        return listResult != null && listResult.getSuccess() && listResult.getData() != null && !listResult.getData()
            .isEmpty();
    }

    /**
     * 将当前的类型转换成另外一个类型
     *
     * @param mapper 转换的方法
     * @param <R>    返回的类型
     * @return 分页返回对象
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
