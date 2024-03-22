package ai.chat2db.server.domain.api.param.team;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * create
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamCreateParam {
    /**
     * 团队编码
     */
    @NotNull
    private String code;

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
