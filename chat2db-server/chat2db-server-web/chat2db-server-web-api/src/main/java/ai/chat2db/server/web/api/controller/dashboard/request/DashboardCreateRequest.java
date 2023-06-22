package ai.chat2db.server.web.api.controller.dashboard.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version DashboardSaveParam.java, v 0.1 2023年06月09日 15:29 moji Exp $
 * @date 2023/06/09
 */
@Data
public class DashboardCreateRequest {

    /**
     * 报表名称
     */
    private String name;

    /**
     * 报表布局信息
     */
    private String schema;

    /**
     * 图表ID列表
     */
    private List<Long> chartIds;
}
