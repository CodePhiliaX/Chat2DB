package ai.chat2db.server.tools.base.wrapper.param;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.enums.OrderByDirectionEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Range;

/**
 * Parameters of paging query
 *
 * @author zhuangjiaju
 * @date 2021/06/26
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class PageQueryParam implements Serializable {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * page number
     */
    @NotNull(message = "Pagination page number cannot be empty")
    @Min(value = 1, message = "Pagination page number must be greater than 0")
    private Integer pageNo;
    /**
     * Paging Size
     */
    @NotNull(message = "Paging size cannot be empty")
    @Range(min = 1, max = EasyToolsConstant.MAX_PAGE_SIZE,
        message = "Paging size must be between 1-" + EasyToolsConstant.MAX_PAGE_SIZE)
    private Integer pageSize;

    /**
     * Whether to return the total number of items
     * Not returned by default to improve performance
     */
    private Boolean enableReturnCount;

    /**
     * sort
     */
    private List<OrderBy> orderByList;

    public PageQueryParam() {
        this.pageNo = 1;
        this.pageSize = 100;
        this.enableReturnCount = Boolean.FALSE;
    }

    /**
     * Query all data
     */
    public void queryAll() {
        this.pageNo = 1;
        this.pageSize = Integer.MAX_VALUE;
    }

    /**
     * Query 1 piece of data
     */
    public void queryOne() {
        this.pageNo = 1;
        this.pageSize = 1;
    }

    /**
     * Add a new sort and replace the original sort
     *
     * @param orderBy sort
     * @return Sorting parameters
     */
    public PageQueryParam orderBy(OrderBy orderBy) {
        orderByList = new ArrayList<>();
        orderByList.add(orderBy);
        return this;
    }

    /**
     * Add a new sort and replace the original sort
     *
     * @param orderConditionName sort field
     * @param direction          Sorting direction
     * @return Sorting parameters
     */
    public PageQueryParam orderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return orderBy(new OrderBy(orderConditionName, direction));
    }

    /**
     * Add a new sort and replace the original sort
     *
     * @param orderCondition Sorting conditions
     * @return Sorting parameters
     */
    public PageQueryParam orderBy(OrderCondition orderCondition) {
        return orderBy(orderCondition.getOrderBy());
    }

    /**
     * Add a new sort
     *
     * @param orderBy sort
     * @return Sorting parameters
     */
    public PageQueryParam andOrderBy(OrderBy orderBy) {
        orderByList.add(orderBy);
        return this;
    }

    /**
     * Add a new sort
     *
     * @param orderConditionName sort field
     * @param direction          Sorting direction
     * @return Sorting parameters
     */
    public PageQueryParam andOrderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return andOrderBy(new OrderBy(orderConditionName, direction));
    }

    /**
     * Add a new sort
     *
     * @param orderCondition Sorting conditions
     * @return Sorting parameters
     */
    public PageQueryParam andOrderBy(OrderCondition orderCondition) {
        return andOrderBy(orderCondition.getOrderBy());
    }
}

