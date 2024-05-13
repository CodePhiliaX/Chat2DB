
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
     * primary key
     */
    private Long id;

    /**
     * team coding
     */
    private String code;

    /**
     * Team Name
     */
    private String name;


    /**
     * Team status
     *
     * @see ai.chat2db.server.domain.api.enums.ValidStatusEnum
     */
    private String status;

}
