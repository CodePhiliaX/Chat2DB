package ai.chat2db.server.domain.api.param.datasource;

import java.util.List;
import lombok.Data;

/**
 * 数据源连接排序更新参数
 *
 * @author chat2db
 */
@Data
public class DataSourceSortUpdateParam {

    /**
     * 按目标顺序排列的数据源 ID
     */
    private List<Long> idList;
}
