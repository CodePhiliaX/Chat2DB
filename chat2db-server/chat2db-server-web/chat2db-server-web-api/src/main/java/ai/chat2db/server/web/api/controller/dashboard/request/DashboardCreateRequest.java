package ai.chat2db.server.web.api.controller.dashboard.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version DashboardSaveParam.java, v 0.1 June 9, 2023 15:29 moji Exp $
 * @date 2023/06/09
 */
@Data
public class DashboardCreateRequest {

    /**
     * Dashboard name
     */
    private String name;

    /**
     * Dashboard layout information
     */
    private String schema;

    /**
     * Chart ID list
     */
    private List<Long> chartIds;
}
