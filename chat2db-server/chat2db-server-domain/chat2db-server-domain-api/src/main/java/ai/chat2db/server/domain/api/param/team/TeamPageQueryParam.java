package ai.chat2db.server.domain.api.param.team;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.Data;

/**
 * page query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamPageQueryParam extends PageQueryParam {

    /**
     * searchKey
     */
    private String searchKey;

}
