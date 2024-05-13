package ai.chat2db.server.domain.api.param.operation;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.Data;

/**
 * @author moji
 * @version UserSavedDdlPageQueryParam.java, v 0.1 September 25, 2022 14:05 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationPageQueryParam extends PageQueryParam {

    /**
     * Data source connection ID
     */
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;

    /**
     * ddl statement status: DRAFT/RELEASE
     */
    private String status;

    /**
     * search keyword
     */
    private String searchKey;

    /**
     * Whether it is opened in the tab, y means open, n means not opened
     */
    private String tabOpened;

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

    /**
     * user id
     */
    private Long userId;
}
