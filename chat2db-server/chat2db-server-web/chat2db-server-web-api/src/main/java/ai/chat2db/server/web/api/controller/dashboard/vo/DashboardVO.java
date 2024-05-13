package ai.chat2db.server.web.api.controller.dashboard.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version Dashboard.java, v 0.1 June 9, 2023 15:32 moji Exp $
 * @date 2023/06/09
 */
@Data
public class DashboardVO {

    /**
     * primary key
     */
    private Long id;

    /**
     * creation time
     */
    private Date gmtCreate;

    /**
     * modified time
     */
    private Date gmtModified;

    /**
     * Dashboard name
     */
    private String name;

    /**
     * Dashboard description
     */
    private String description;

    /**
     * Dashboard layout information
     */
    private String schema;

    /**
     * Chart ID list
     */
    private List<Long> chartIds;
}
