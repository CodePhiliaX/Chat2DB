
package ai.chat2db.server.admin.api.controller.common.request;

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
