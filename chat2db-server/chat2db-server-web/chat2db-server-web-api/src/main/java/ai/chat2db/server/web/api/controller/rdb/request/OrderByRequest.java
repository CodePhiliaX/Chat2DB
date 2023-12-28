package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import ai.chat2db.spi.model.OrderBy;
import lombok.Data;

import java.util.List;

@Data
public class OrderByRequest extends DataSourceBaseRequest implements DataSourceBaseRequestInfo {

    /**
     * origin sql
     */
    private String originSql;

    /**
     * 排序字段
     */
    private List<OrderBy> orderByList;

}
