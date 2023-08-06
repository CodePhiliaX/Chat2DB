package ai.chat2db.server.domain.api.param.team.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * select
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUserSelector {
    /**
     * 团队
     */
    private Boolean team;

    /**
     * 用户
     */
    private Boolean user;
}
