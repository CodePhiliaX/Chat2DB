package ai.chat2db.server.domain.api.param.operation;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.Data;

/**
 * @author moji
 * @version UserExecutedDdlPageQueryParam.java, v 0.1 2022年09月25日 14:05 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationLogPageQueryParam extends PageQueryParam {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 搜索关键词
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
