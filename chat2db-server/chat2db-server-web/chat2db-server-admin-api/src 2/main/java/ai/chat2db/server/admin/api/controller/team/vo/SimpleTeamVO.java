
package ai.chat2db.server.admin.api.controller.team.vo;

import lombok.Data;

/**
 * team
 *
 * @author Jiaju Zhuang
 */
@Data
public class SimpleTeamVO {

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

}
