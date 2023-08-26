package ai.chat2db.server.tools.common.model;

import java.util.List;

import ai.chat2db.server.tools.base.wrapper.param.OrderBy;
import ai.chat2db.server.tools.common.util.EasySqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.collections4.CollectionUtils;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ORDER_BY;

/**
 * Custom query wrapper
 *
 * @author Jiaju Zhuang
 */
public class EasyLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {
    public void orderBy(List<OrderBy> orderByList) {
        if (CollectionUtils.isEmpty(orderByList)) {
            return;
        }
        for (OrderBy orderBy : orderByList) {
            appendSqlSegments(ORDER_BY, EasySqlUtils.columnToSqlSegment(orderBy.getOrderConditionName()),
                EasySqlUtils.parseOrderBy(orderBy.getDirection()));
        }
    }
}
