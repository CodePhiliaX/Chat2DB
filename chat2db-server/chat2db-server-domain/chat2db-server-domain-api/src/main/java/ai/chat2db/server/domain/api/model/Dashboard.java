package ai.chat2db.server.domain.api.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version Dashboard.java, v 0.1 June 9, 2023 15:32 moji Exp $
 * @date 2023/06/09
 */
@Data
public class Dashboard {

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
     * Whether it has been deleted, y means deleted, n means not deleted
     */
    private String deleted;

    /**
     * user id
     */
    private Long userId;

    /**
     * Chart ID list
     */
    private List<Long> chartIds;

}
