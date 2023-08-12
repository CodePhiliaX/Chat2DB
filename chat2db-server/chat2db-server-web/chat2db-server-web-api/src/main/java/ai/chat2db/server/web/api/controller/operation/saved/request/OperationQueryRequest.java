package ai.chat2db.server.web.api.controller.operation.saved.request;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 2022年09月18日 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationQueryRequest extends PageQueryRequest {

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * DB名称
     */
    private String databaseName;

    /**
     * 模糊搜索词
     */
    private String searchKey;

    /**
     * 是否在tab中被打开,y表示打开,n表示未打开
     */
    private String tabOpened;

    /**
     * ddl语句状态:DRAFT/RELEASE
     */
    private String status;

    /**
     * orderBy modify time desc
     */
    private Boolean orderByDesc;

    /**
     * operation type
     */
    private String operationType;
}
