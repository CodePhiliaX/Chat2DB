package ai.chat2db.server.admin.api.controller.team.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * update
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUpdateRequest {
    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 团队名称
     */
    @NotNull
    private String name;

    /**
     * 团队状态
     *
     * @see ai.chat2db.server.domain.api.enums.ValidStatusEnum
     */
    @NotNull
    private String status;

    /**
     * 角色编码
     *
     * @see ai.chat2db.server.domain.api.enums.RoleCodeEnum
     */
    @NotNull
    private String roleCode;

    /**
     * 团队描述
     */
    private String description;
}
