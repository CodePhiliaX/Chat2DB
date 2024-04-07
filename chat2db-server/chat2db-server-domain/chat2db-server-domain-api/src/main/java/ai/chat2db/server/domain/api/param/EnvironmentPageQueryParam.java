package ai.chat2db.server.domain.api.param;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import lombok.Data;

/**
 * environment
 *
 * @author Jiaju Zhuang
 */
@Data
public class EnvironmentPageQueryParam extends PageQueryParam {

    /**
     * search keyword
     */
    private String searchKey;
}
