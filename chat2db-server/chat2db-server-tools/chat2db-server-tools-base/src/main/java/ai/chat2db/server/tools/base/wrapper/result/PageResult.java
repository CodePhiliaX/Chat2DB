package ai.chat2db.server.tools.base.wrapper.result;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.wrapper.Result;
import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult;
import ai.chat2db.server.tools.base.wrapper.result.web.WebPageResult.Page;
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
public class PageResult<T> implements Serializable, Result<List<T>> {
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
     * 分页编码
     */
    private Integer pageNo;
    /**
     * 分页大小
     */
    private Integer pageSize;
    /**
     * 总的大小
     */
    private Long total;
    /**
     * traceId
     */
    private String traceId;
    /**
     * 是否存在下一页
     */
    private Boolean hasNextPage;

    /**
     * error detail
     */
    private String errorDetail;

    /**
     * solution link
     */
    private String solutionLink;

    public PageResult() {
        this.pageNo = 1;
        this.pageSize = 10;
        this.total = 0L;
        this.success = Boolean.TRUE;
    }

    private PageResult(List<T> data, Long total, Long pageNo, Long pageSize) {
        this();
        this.data = data;
        this.total = total;
        if (pageNo != null) {
            this.pageNo = Math.toIntExact(pageNo);
        }
        if (pageSize != null) {
            this.pageSize = Math.toIntExact(pageSize);
        }
    }

    private PageResult(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        this();
        this.data = data;
        this.total = total;
        if (pageNo != null) {
            this.pageNo = pageNo;
        }
        if (pageSize != null) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 构建分页返回对象
     *
     * @param data     返回的对象
     * @param total    总的条数
     * @param pageNo   页码
     * @param pageSize 分页大小
     * @param <T>      返回的对象类型
     * @return 分页返回对象
     */
    public static <T> PageResult<T> of(List<T> data, Long total, Long pageNo, Long pageSize) {
        return new PageResult<>(data, total, pageNo, pageSize);
    }

    /**
     * 构建分页返回对象
     *
     * @param data     返回的对象
     * @param total    总的条数
     * @param pageNo   页码
     * @param pageSize 分页大小
     * @param <T>      返回的对象类型
     * @return 分页返回对象
     */
    public static <T> PageResult<T> of(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        return new PageResult<>(data, total, pageNo, pageSize);
    }

    /**
     * 构建分页返回对象
     *
     * @param data  返回的对象
     * @param total 总的条数
     * @param param 分页参数
     * @param <T>   返回的对象类型
     * @return 分页返回对象
     */
    public static <T> PageResult<T> of(List<T> data, Long total, PageQueryParam param) {
        return new PageResult<>(data, total, param.getPageNo(), param.getPageSize());
    }

    /**
     * 构建分页返回对象
     * 总条数返回
     *
     * @param data  返回的对象
     * @param param 分页参数
     * @param <T>   返回的对象类型
     * @return 分页返回对象
     */
    public static <T> PageResult<T> of(List<T> data, PageQueryParam param) {
        return new PageResult<>(data, 0L, param.getPageNo(), param.getPageSize());
    }

    /**
     * 构建空的返回对象
     *
     * @param pageNo   页码
     * @param pageSize 分页大小
     * @param <T>      返回的对象类型
     * @return 分页返回对象
     */
    public static <T> PageResult<T> empty(Long pageNo, Long pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    /**
     * 构建空的返回对象
     *
     * @param pageNo   页码
     * @param pageSize 分页大小
     * @param <T>      返回的对象类型
     * @return 分页返回对象
     */
    public static <T> PageResult<T> empty(Integer pageNo, Integer pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    /**
     * 判断是否还有下一页
     * 根据分页大小来计算 防止total为空
     *
     * @return 是否还有下一页
     */
    public Boolean calculateHasNextPage() {
        // 存在分页大小 根据分页来计算
        if (total > 0) {
            return (long)pageSize * pageNo <= total;
        }
        // 没有数据 肯定没有下一页
        if (data == null || data.isEmpty()) {
            return false;
        }
        // 当前数量小于分页数量
        return data.size() >= pageSize;
    }

    /**
     * 判断是否还有下一页
     * 根据分页大小来计算 防止total为空
     *
     * @return 是否还有下一页
     * @deprecated 使用 {@link #getHasNextPage()} ()}
     */
    @Deprecated
    public boolean hasNextPage() {
        return getHasNextPage();
    }

    public Boolean getHasNextPage() {
        if (hasNextPage == null) {
            hasNextPage = calculateHasNextPage();
        }
        return hasNextPage;
    }

    /**
     * 判断是否存在数据
     *
     * @return 是否存在数据
     */
    public boolean hasData() {
        return hasData(this);
    }

    /**
     * 返回查询异常信息
     *
     * @param errorCode    错误编码
     * @param errorMessage 错误信息
     * @param <T>          返回的对象
     * @return 分页返回对象
     */
    public static <T> PageResult<T> error(String errorCode, String errorMessage) {
        PageResult<T> result = new PageResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.FALSE;
        return result;
    }

    /**
     * 判断是否存在数据
     *
     * @param pageResult
     * @return 是否存在数据
     */
    public static boolean hasData(PageResult<?> pageResult) {
        return pageResult != null && pageResult.getSuccess() && pageResult.getData() != null && !pageResult.getData()
            .isEmpty();
    }

    /**
     * 将当前的类型转换成另外一个类型
     *
     * @param mapper 转换的方法
     * @param <R>    返回的类型
     * @return 分页返回对象
     */
    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().stream().map(mapper).collect(Collectors.toList())
            : Collections.emptyList();
        PageResult<R> pageResult = new PageResult<>();
        pageResult.setSuccess(getSuccess());
        pageResult.setErrorCode(getErrorCode());
        pageResult.setErrorMessage(getErrorMessage());
        pageResult.setData(returnData);
        pageResult.setPageNo(getPageNo());
        pageResult.setPageSize(getPageSize());
        pageResult.setTotal(getTotal());
        pageResult.setTraceId(getTraceId());
        return pageResult;
    }

    /**
     * 将当前的类型转换成另外一个类型
     *
     * @param mapper 转换的方法
     * @param <R>    返回的类型
     * @return 分页返回对象
     */
    public <R> ListResult<R> mapToList(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().stream().map(mapper).collect(Collectors.toList())
            : Collections.emptyList();
        ListResult<R> result = new ListResult<>();
        result.setSuccess(getSuccess());
        result.setErrorCode(getErrorCode());
        result.setErrorMessage(getErrorMessage());
        result.setTraceId(getTraceId());
        result.setData(returnData);
        return result;
    }

    /**
     * 将当前的类型转换成另外一个类型
     * 并且转换成web的类型
     * 这里注意如果当前项目在web层用的也是 <code>PageResult</code> 则直接使用 <code>map</code>方法接口即可
     *
     * @param mapper 转换的方法
     * @param <R>    返回的类型
     * @return 分页返回对象
     */
    public <R> WebPageResult<R> mapToWeb(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().stream().map(mapper).collect(Collectors.toList())
            : Collections.emptyList();
        WebPageResult<R> pageResult = new WebPageResult<>();
        pageResult.setSuccess(getSuccess());
        pageResult.setErrorCode(getErrorCode());
        pageResult.setErrorMessage(getErrorMessage());
        pageResult.setTraceId(getTraceId());
        // 重新设置一个分页信息
        Page<R> page = new Page<>();
        pageResult.setData(page);
        page.setData(returnData);
        page.setPageNo(getPageNo());
        page.setPageSize(getPageSize());
        page.setTotal(getTotal());
        pageResult.setData(page);
        return pageResult;
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
