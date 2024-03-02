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
     * primary key
     */
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * team
     */
    private SimpleTeamVO team;
}
