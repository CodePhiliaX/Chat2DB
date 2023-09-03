package ai.chat2db.server.domain.api.param.team;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * update
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUpdateParam {
    /**
     * 主键
     */
    @NotNull
    private Long id;

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
     * 角色编码
     *
     * @see ai.chat2db.server.domain.api.enums.RoleCodeEnum
     */
    private String roleCode;

    /**
     * 团队描述
     */
    private String description;
}
