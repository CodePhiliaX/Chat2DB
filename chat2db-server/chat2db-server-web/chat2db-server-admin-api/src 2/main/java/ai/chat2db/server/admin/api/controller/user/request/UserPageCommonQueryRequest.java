
package ai.chat2db.server.admin.api.controller.user.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class UserPageCommonQueryRequest extends PageQueryRequest {
    /**
     * user id
     */
    @NotNull
    private Long userId;

    /**
     * searchKey
     */
    private String searchKey;
}
