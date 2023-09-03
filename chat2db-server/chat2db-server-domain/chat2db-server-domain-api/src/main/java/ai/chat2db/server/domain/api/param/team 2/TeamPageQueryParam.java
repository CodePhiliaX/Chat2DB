package ai.chat2db.server.domain.api.param.team;

import ai.chat2db.server.tools.base.wrapper.param.OrderBy;
import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.Data;
import lombok.Getter;

/**
 * page query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamPageQueryParam extends PageQueryParam {

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
