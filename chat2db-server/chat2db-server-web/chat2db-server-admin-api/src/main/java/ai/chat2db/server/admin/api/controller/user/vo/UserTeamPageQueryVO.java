
package ai.chat2db.server.admin.api.controller.user.vo;

import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class UserTeamPageQueryVO {

    /**
     * user id
     */
    private Long userId;

    /**
     * 团队编码
     */
    private String code;

    /**
     * 团队名称
     */
    private String name;

    /**
     * 团队状态
     *
     * @see ai.chat2db.server.domain.api.enums.ValidStatusEnum
     */
    private String status;
}
