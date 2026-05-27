package ai.chat2db.server.web.api.controller.data.source.request;

import java.util.List;
import lombok.Data;

/**
 * 数据源连接排序更新请求
 *
 * @author chat2db
 */
@Data
public class DataSourceSortUpdateRequest {

    /**
     * 按目标顺序排列的数据源 ID
     */
    private List<Long> idList;
}
