
package ai.chat2db.server.admin.api.controller.team.vo;

import java.util.Date;

import ai.chat2db.server.common.api.controller.vo.SimpleUserVO;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamPageQueryVO {
    /**
     * 主键
     */
    private Long id;

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

    /**
     * 团队描述
     */
    private String description;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 修改人用户id
     */
    private Long modifiedUserId;

    /**
     * 修改人用户
     */
    private SimpleUserVO modifiedUser;
}
