package ai.chat2db.server.tools.base.wrapper.param;

import java.io.Serializable;

import ai.chat2db.server.tools.base.enums.OrderByDirectionEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序的对象
 *
 * @author 是仪
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBy implements Serializable {
    /**
     * 排序字段
     */
    private String orderConditionName;
    /**
     * 排序方向
     */
    private OrderByDirectionEnum direction;

    public static OrderBy of(String property, OrderByDirectionEnum direction) {
        return new OrderBy(property, direction);
    }

    public static OrderBy asc(String property) {
        return new OrderBy(property, OrderByDirectionEnum.ASC);
    }

    public static OrderBy desc(String property) {
        return new OrderBy(property, OrderByDirectionEnum.DESC);
    }
}