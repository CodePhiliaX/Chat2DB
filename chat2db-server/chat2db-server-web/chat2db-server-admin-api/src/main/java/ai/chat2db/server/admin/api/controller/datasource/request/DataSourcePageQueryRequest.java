
package ai.chat2db.server.admin.api.controller.datasource.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class DataSourcePageQueryRequest extends PageQueryRequest {

    /**
     * searchKey
     */
    private String searchKey;
}
