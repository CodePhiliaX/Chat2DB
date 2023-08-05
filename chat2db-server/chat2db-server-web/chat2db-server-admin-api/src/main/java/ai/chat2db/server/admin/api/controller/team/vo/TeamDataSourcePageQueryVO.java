
package ai.chat2db.server.admin.api.controller.team.vo;

import ai.chat2db.server.admin.api.controller.datasource.vo.SimpleDataSourceVO;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamDataSourcePageQueryVO {

    /**
     * team id
     */
    private Long teamId;

    /**
     * Data Source
     */
    private SimpleDataSourceVO dataSource;
}
