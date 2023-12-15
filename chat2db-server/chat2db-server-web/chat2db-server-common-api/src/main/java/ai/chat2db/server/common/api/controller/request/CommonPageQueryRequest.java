
package ai.chat2db.server.common.api.controller.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import lombok.Data;

/**
 * Common pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class CommonPageQueryRequest extends PageQueryRequest {


    /**
     * searchKey
     */
    private String searchKey;
}
