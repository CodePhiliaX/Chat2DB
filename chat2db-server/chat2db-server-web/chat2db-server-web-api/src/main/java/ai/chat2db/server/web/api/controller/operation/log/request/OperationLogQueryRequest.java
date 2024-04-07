package ai.chat2db.server.web.api.controller.operation.log.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 September 18, 2022 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationLogQueryRequest extends PageQueryRequest {

    /**
     * Fuzzy word search
     */
    private String searchKey;

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * Name database
     */
    private String databaseName;

    /**
     * schema name
     */
    private String schemaName;
}
