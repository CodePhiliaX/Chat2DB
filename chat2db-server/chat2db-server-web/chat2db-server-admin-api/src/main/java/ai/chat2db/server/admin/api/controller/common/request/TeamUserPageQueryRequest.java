
package ai.chat2db.server.admin.api.controller.common.request;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import lombok.Data;

/**
 * Common pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamUserPageQueryRequest extends PageQueryRequest {

    /**
     * 授权类型
     *
     * @see AccessObjectTypeEnum
     */
    private String type;

    /**
     * searchKey
     */
    private String searchKey;
}
