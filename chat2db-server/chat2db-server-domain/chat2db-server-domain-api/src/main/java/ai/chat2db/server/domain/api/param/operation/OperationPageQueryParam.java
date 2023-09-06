package ai.chat2db.server.domain.api.param.operation;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.Data;

/**
 * @author moji
 * @version UserSavedDdlPageQueryParam.java, v 0.1 2022年09月25日 14:05 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationPageQueryParam extends PageQueryParam {

    /**
     * 数据源连接ID
     */
    private Long dataSourceId;

    /**
     * db名称
     */
    private String databaseName;

    /**
     * ddl语句状态:DRAFT/RELEASE
     */
    private String status;

    /**
     * 搜索关键词
     */
    private String searchKey;

    /**
     * 是否在tab中被打开,y表示打开,n表示未打开
     */
    private String tabOpened;

    /**
     * orderBy modify time desc
     */
    private boolean orderByDesc;

    /**
     * operation type
     */
    private String operationType;

    /**
     * 用户id
     */
    private Long userId;
}
