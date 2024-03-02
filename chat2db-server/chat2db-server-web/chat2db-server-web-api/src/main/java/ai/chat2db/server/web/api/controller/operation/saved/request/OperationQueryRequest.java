package ai.chat2db.server.web.api.controller.operation.saved.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 September 18, 2022 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationQueryRequest extends PageQueryRequest {

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * Fuzzy search terms
     */
    private String searchKey;

    /**
     * Whether it is opened in the tab, y means open, n means not opened
     */
    private String tabOpened;

    /**
     * ddl statement status: DRAFT/RELEASE
     */
    private String status;

    /**
     * orderBy modify time desc
     */
    private Boolean orderByDesc;

    /**
     * orderBy create time desc
     */
    private Boolean orderByCreateDesc;

    /**
     * operation type
     */
    private String operationType;
}
