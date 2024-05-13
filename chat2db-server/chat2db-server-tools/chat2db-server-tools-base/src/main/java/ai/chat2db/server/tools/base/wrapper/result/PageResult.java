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
 * data return object
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class PageResult<T> implements Serializable, Result<List<T>> {
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
     * Page coding
     */
    private Integer pageNo;
    /**
     * Paging Size
     */
    private Integer pageSize;
    /**
     * Total
     */
    private Long total;
    /**
     * traceId
     */
    private String traceId;
    /**
     * Is there a next page?
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
     * Construct paging return object
     *
     * @param data object returned
     * @param total total number of items
     * @param pageNo page number
     * @param pageSize paging size
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> PageResult<T> of(List<T> data, Long total, Long pageNo, Long pageSize) {
        return new PageResult<>(data, total, pageNo, pageSize);
    }

    /**
     * Construct paging return object
     *
     * @param data object returned
     * @param total total number of items
     * @param pageNo page number
     * @param pageSize paging size
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> PageResult<T> of(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        return new PageResult<>(data, total, pageNo, pageSize);
    }

    /**
     * Construct paging return object
     *
     * @param data object returned
     * @param total total number of items
     * @param param paging parameters
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> PageResult<T> of(List<T> data, Long total, PageQueryParam param) {
        return new PageResult<>(data, total, param.getPageNo(), param.getPageSize());
    }

    /**
     * Construct paging return object
     * Total number of items returned
     *
     * @param data object returned
     * @param param paging parameters
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> PageResult<T> of(List<T> data, PageQueryParam param) {
        return new PageResult<>(data, 0L, param.getPageNo(), param.getPageSize());
    }

    /**
     * Construct an empty return object
     *
     * @param pageNo page number
     * @param pageSize paging size
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> PageResult<T> empty(Long pageNo, Long pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    /**
     * Construct an empty return object
     *
     * @param pageNo page number
     * @param pageSize paging size
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> PageResult<T> empty(Integer pageNo, Integer pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    /**
     * Determine whether there is a next page
     * Calculated based on paging size to prevent total from being empty
     *
     * @return Is there a next page?
     */
    public Boolean calculateHasNextPage() {
        // There is a paging size calculated based on the paging
        if (total > 0) {
            return (long)pageSize * pageNo <= total;
        }
        // No data, definitely no next page
        if (data == null || data.isEmpty()) {
            return false;
        }
        // The current number is less than the number of pages
        return data.size() >= pageSize;
    }

    /**
     * Determine whether there is a next page
     * Calculated based on paging size to prevent total from being empty
     *
     * @return Is there a next page?
     * @deprecated using {@link #getHasNextPage()} ()}
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
     * Determine whether data exists
     *
     * @return whether data exists
     */
    public boolean hasData() {
        return hasData(this);
    }

    /**
     * Return query exception information
     *
     * @param errorCode error coding
     * @param errorMessage error message
     * @param <T> The returned object
     * @return paging return object
     */
    public static <T> PageResult<T> error(String errorCode, String errorMessage) {
        PageResult<T> result = new PageResult<>();
        result.errorCode = errorCode;
        result.errorMessage = errorMessage;
        result.success = Boolean.FALSE;
        return result;
    }

    /**
     * Determine whether data exists
     *
     * @param pageResult
     * @return whether data exists
     */
    public static boolean hasData(PageResult<?> pageResult) {
        return pageResult != null && pageResult.getSuccess() && pageResult.getData() != null && !pageResult.getData()
            .isEmpty();
    }

    /**
     * Convert the current type to another type
     *
     * @param mapper conversion method
     * @param <R> Return type
     * @return paging return object
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
     * Convert the current type to another type
     *
     * @param mapper conversion method
     * @param <R> Return type
     * @return paging return object
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
     * Convert the current type to another type
     * and converted to web type
     * Note here that if the current project also uses <code>PageResult</code> in the web layer, you can directly use the <code>map</code> method interface.
     *
     * @param mapper conversion method
     * @param <R> Return type
     * @return paging return object
     */
    public <R> WebPageResult<R> mapToWeb(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().stream().map(mapper).collect(Collectors.toList())
            : Collections.emptyList();
        WebPageResult<R> pageResult = new WebPageResult<>();
        pageResult.setSuccess(getSuccess());
        pageResult.setErrorCode(getErrorCode());
        pageResult.setErrorMessage(getErrorMessage());
        pageResult.setTraceId(getTraceId());
        // Reset a paging information
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
