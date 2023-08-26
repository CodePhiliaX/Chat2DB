package ai.chat2db.server.domain.api.param.user;

import ai.chat2db.server.tools.base.wrapper.param.OrderBy;
import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * * page query
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageQueryParam extends PageQueryParam {

    /**
     * searchKey
     */
    private String searchKey;

    @Getter
    public enum OrderCondition implements ai.chat2db.server.tools.base.wrapper.param.OrderCondition {
        ID_DESC(OrderBy.desc("id")),
        ;

        final OrderBy orderBy;

        OrderCondition(OrderBy orderBy) {
            this.orderBy = orderBy;
        }
    }
}
