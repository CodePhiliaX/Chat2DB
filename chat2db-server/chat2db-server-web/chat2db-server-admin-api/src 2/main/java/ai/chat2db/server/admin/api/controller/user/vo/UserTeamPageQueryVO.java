
package ai.chat2db.server.admin.api.controller.user.vo;

import ai.chat2db.server.admin.api.controller.team.vo.SimpleTeamVO;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class UserTeamPageQueryVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * 团队
     */
    private SimpleTeamVO team;
}
