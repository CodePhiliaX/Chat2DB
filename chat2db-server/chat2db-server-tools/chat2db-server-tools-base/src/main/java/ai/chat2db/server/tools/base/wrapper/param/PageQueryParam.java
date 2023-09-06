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
 * 分页查询的参数
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
     * 页码
     */
    @NotNull(message = "分页页码不能为空")
    @Min(value = 1, message = "分页页码必须大于0")
    private Integer pageNo;
    /**
     * 分页大小
     */
    @NotNull(message = "分页大小不能为空")
    @Range(min = 1, max = EasyToolsConstant.MAX_PAGE_SIZE,
        message = "分页大小必须在1-" + EasyToolsConstant.MAX_PAGE_SIZE + "之间")
    private Integer pageSize;

    /**
     * 是否返回总条数
     * 默认不返回 提高性能
     */
    private Boolean enableReturnCount;

    /**
     * 排序
     */
    private List<OrderBy> orderByList;

    public PageQueryParam() {
        this.pageNo = 1;
        this.pageSize = 100;
        this.enableReturnCount = Boolean.FALSE;
    }

    /**
     * 查询全部数据
     */
    public void queryAll() {
        this.pageNo = 1;
        this.pageSize = Integer.MAX_VALUE;
    }

    /**
     * 查询1条加速
     */
    public void queryOne() {
        this.pageNo = 1;
        this.pageSize = 1;
    }

    /**
     * 新增一个排序 并替换原有排序
     *
     * @param orderBy 排序
     * @return 排序参数
     */
    public PageQueryParam orderBy(OrderBy orderBy) {
        orderByList = new ArrayList<>();
        orderByList.add(orderBy);
        return this;
    }

    /**
     * 新增一个排序 并替换原有排序
     *
     * @param orderConditionName 排序字段
     * @param direction          排序方向
     * @return 排序参数
     */
    public PageQueryParam orderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return orderBy(new OrderBy(orderConditionName, direction));
    }

    /**
     * 新增一个排序 并替换原有排序
     *
     * @param orderCondition 排序条件
     * @return 排序参数
     */
    public PageQueryParam orderBy(OrderCondition orderCondition) {
        return orderBy(orderCondition.getOrderBy());
    }

    /**
     * 新增一个排序
     *
     * @param orderBy 排序
     * @return 排序参数
     */
    public PageQueryParam andOrderBy(OrderBy orderBy) {
        orderByList.add(orderBy);
        return this;
    }

    /**
     * 新增一个排序
     *
     * @param orderConditionName 排序字段
     * @param direction          排序方向
     * @return 排序参数
     */
    public PageQueryParam andOrderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return andOrderBy(new OrderBy(orderConditionName, direction));
    }

    /**
     * 新增一个排序
     *
     * @param orderCondition 排序条件
     * @return 排序参数
     */
    public PageQueryParam andOrderBy(OrderCondition orderCondition) {
        return andOrderBy(orderCondition.getOrderBy());
    }
}

