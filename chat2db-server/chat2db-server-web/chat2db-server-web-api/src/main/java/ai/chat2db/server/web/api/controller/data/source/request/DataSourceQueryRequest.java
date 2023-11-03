package ai.chat2db.server.web.api.controller.data.source.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import lombok.Data;

/**
 * @author moji
 * @version ConnectionQueryRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DataSourceQueryRequest extends PageQueryRequest {

    /**
     * 别名模糊搜索词
     */
    private String searchKey;
    /**
     * 连接类型
     *
     * @see ai.chat2db.server.domain.api.enums.DataSourceKindEnum
     */
    private String kind;
}
