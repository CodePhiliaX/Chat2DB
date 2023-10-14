package ai.chat2db.server.web.api.controller.operation.log.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 2022年09月18日 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationLogQueryRequest extends PageQueryRequest {

    /**
     * 模糊词搜索
     */
    private String searchKey;

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * schema名称
     */
    private String schemaName;
}
