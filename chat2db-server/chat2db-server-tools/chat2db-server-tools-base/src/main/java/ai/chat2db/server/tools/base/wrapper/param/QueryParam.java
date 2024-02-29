package ai.chat2db.server.tools.base.wrapper.param;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ai.chat2db.server.tools.base.constant.EasyToolsConstant;
import ai.chat2db.server.tools.base.enums.OrderByDirectionEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * query parameters
 *
 * @author zhuangjiaju
 * @date 2021/06/26
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class QueryParam implements Serializable {
    @Serial
    private static final long serialVersionUID = EasyToolsConstant.SERIAL_VERSION_UID;

    /**
     * sort
     */
    private List<OrderBy> orderByList;

    /**
     * Add a new sort and replace the original sort
     *
     * @param orderBy sort
     * @return Sorting parameters
     */
    public QueryParam orderBy(OrderBy orderBy) {
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
    public QueryParam orderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return orderBy(new OrderBy(orderConditionName, direction));
    }

    /**
     * Add a new sort and replace the original sort
     *
     * @param orderCondition Sorting conditions
     * @return Sorting parameters
     */
    public QueryParam orderBy(OrderCondition orderCondition) {
        return orderBy(orderCondition.getOrderBy());
    }

    /**
     * Add a new sort
     *
     * @param orderBy sort
     * @return Sorting parameters
     */
    public QueryParam andOrderBy(OrderBy orderBy) {
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
    public QueryParam andOrderBy(String orderConditionName, OrderByDirectionEnum direction) {
        return andOrderBy(new OrderBy(orderConditionName, direction));
    }

    /**
     * Add a new sort
     *
     * @param orderCondition Sorting conditions
     * @return Sorting parameters
     */
    public QueryParam andOrderBy(OrderCondition orderCondition) {
        return andOrderBy(orderCondition.getOrderBy());
    }

}
