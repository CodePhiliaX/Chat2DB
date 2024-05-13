package ai.chat2db.server.tools.base.wrapper.result.web;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.wrapper.Result;
import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 *The return object of data
 * Consistent with PageResult, you can also use PageResult directly.
 * This is an additional class created because the front end of some projects needs to encapsulate data+pageNo together.
 *
 * @author Shi Yi
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class WebPageResult<T> implements Serializable, Result<List<T>> {
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;
    /**
     * whether succeed
     *
     * @mock true
     */
    private Boolean success;
    /**
     * Exception coding
     */
    private String errorCode;
    /**
     * Exception information
     */
    private String errorMessage;
    /**
     * Data information
     */
    private Page<T> data;
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

    public WebPageResult() {
        this.success = Boolean.TRUE;
        this.data = new Page<>();
    }

    private WebPageResult(List<T> data, Long total, Long pageNo, Long pageSize) {
        this.success = Boolean.TRUE;
        this.data = new Page<>(data, total, pageNo, pageSize);
    }

    private WebPageResult(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        this.success = Boolean.TRUE;
        this.data = new Page<>(data, total, pageNo, pageSize);
    }

    /**
     * Build pagination return object
     *
     * @param data object returned
     * @param total total number of items
     * @param pageNo page number
     * @param pageSize paging size
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> WebPageResult<T> of(List<T> data, Long total, Long pageNo, Long pageSize) {
        return new WebPageResult<>(data, total, pageNo, pageSize);
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
    public static <T> WebPageResult<T> of(List<T> data, Long total, Integer pageNo, Integer pageSize) {
        return new WebPageResult<>(data, total, pageNo, pageSize);
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
    public static <T> WebPageResult<T> of(List<T> data, Long total, PageQueryParam param) {
        return new WebPageResult<>(data, total, param.getPageNo(), param.getPageSize());
    }

    /**
     * Construct an empty return object
     *
     * @param pageNo page number
     * @param pageSize paging size
     * @param <T> The returned object type
     * @return paging return object
     */
    public static <T> WebPageResult<T> empty(Long pageNo, Long pageSize) {
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
    public static <T> WebPageResult<T> empty(Integer pageNo, Integer pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
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
        if (data == null) {
            return Boolean.FALSE;
        }
        return data.getHasNextPage();
    }

    /**
     * Return query exception information
     *
     * @param errorCode error code
     * @param errorMessage error message
     * @param <T> The returned object
     * @return paging return object
     */
    public static <T> WebPageResult<T> error(String errorCode, String errorMessage) {
        WebPageResult<T> result = new WebPageResult<>();
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
    public static boolean hasData(WebPageResult<?> pageResult) {
        return pageResult != null && pageResult.getSuccess() && pageResult.getData() != null
            && pageResult.getData().getData() != null && !pageResult.getData().getData().isEmpty();
    }

    /**
     * Convert the current type to another type
     *
     * @param mapper conversion method
     * @param <R> Return type
     * @return paging return object
     */
    public <R> WebPageResult<R> map(Function<T, R> mapper) {
        List<R> returnData = hasData(this) ? getData().getData().stream().map(mapper).collect(Collectors.toList())
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
        page.setPageNo(data.getPageNo());
        page.setPageSize(data.getPageSize());
        page.setTotal(data.getTotal());
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

    /**
     * Pagination information
     *
     * @param <T>
     */
    @Data
    public static class Page<T> {
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
         * Is there a next page?
         */
        private Boolean hasNextPage;

        public Page() {
            this.pageNo = 1;
            this.pageSize = 10;
            this.total = 0L;
        }

        private Page(List<T> data, Long total, Long pageNo, Long pageSize) {
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

        private Page(List<T> data, Long total, Integer pageNo, Integer pageSize) {
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

        public Boolean getHasNextPage() {
            if (hasNextPage == null) {
                hasNextPage = calculateHasNextPage();
            }
            return hasNextPage;
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
    }
}
