
package ai.chat2db.server.admin.api.controller.datasource.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Common pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class DataSourceAccessPageQueryRequest extends PageQueryRequest {

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * searchKey
     */
    private String searchKey;
}
