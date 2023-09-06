package ai.chat2db.server.domain.api.param.datasource;

import ai.chat2db.server.tools.base.wrapper.param.OrderBy;
import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.Data;
import lombok.Getter;

/**
 * @author moji
 * @version DataSourcePageQueryParam.java, v 0.1 2022年09月23日 15:27 moji Exp $
 * @date 2022/09/23
 */
@Data
public class DataSourcePageQueryParam extends PageQueryParam {

    /**
     * 搜索关键词
     */
    private String searchKey;

    /**
     * 连接类型
     *
     * @see ai.chat2db.server.domain.api.enums.DataSourceKindEnum
     */
    private String kind;

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
