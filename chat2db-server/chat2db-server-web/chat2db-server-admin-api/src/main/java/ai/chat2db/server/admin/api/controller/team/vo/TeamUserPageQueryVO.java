
package ai.chat2db.server.admin.api.controller.team.vo;

import ai.chat2db.server.admin.api.controller.user.vo.SimpleUserVO;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUserPageQueryVO {
    /**
     * primary key
     */
    private Long id;

    /**
     * team id
     */
    private Long teamId;

    /**
     * user
     */
    private SimpleUserVO user;
}
