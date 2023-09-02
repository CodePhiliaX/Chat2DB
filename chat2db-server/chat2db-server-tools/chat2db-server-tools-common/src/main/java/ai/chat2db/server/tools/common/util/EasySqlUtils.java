package ai.chat2db.server.tools.common.util;

import java.util.Arrays;
import java.util.List;

import ai.chat2db.server.tools.base.enums.OrderByDirectionEnum;
import ai.chat2db.server.tools.base.wrapper.param.OrderBy;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.segments.ColumnSegment;
import com.baomidou.mybatisplus.core.conditions.segments.OrderBySegmentList;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import org.apache.commons.collections4.CollectionUtils;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.ASC;
import static com.baomidou.mybatisplus.core.enums.SqlKeyword.DESC;

/**
 * sql utils
 *
 * @author Jiaju Zhuang
 */
public class EasySqlUtils {

    public static String orderBy(List<OrderBy> orderByList) {
        if (CollectionUtils.isEmpty(orderByList)) {
            return null;
        }
        OrderBySegmentList orderBySegmentList = new OrderBySegmentList();
        for (OrderBy orderBy : orderByList) {
            orderBySegmentList.addAll(
                Arrays.asList(SqlKeyword.ORDER_BY, columnToSqlSegment(orderBy.getOrderConditionName()),
                    parseOrderBy(orderBy.getDirection())));
        }
        return orderBySegmentList.getSqlSegment();
    }

    /**
     * 获取 columnName
     */
    public static ColumnSegment columnToSqlSegment(String column) {
        return () -> column;
    }

    public static ISqlSegment parseOrderBy(OrderByDirectionEnum direction) {
        if (direction == OrderByDirectionEnum.ASC) {
            return ASC;
        }
        return DESC;
    }

    public static String buildLikeRightFuzzy(String param) {
        if (param == null) {
            return null;
        }
        return param + "%";
    }
}
