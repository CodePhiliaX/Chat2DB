package ai.chat2db.server.domain.api.param.operation;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.Data;

/**
 * @author moji
 * @version UserExecutedDdlPageQueryParam.java, v 0.1 September 25, 2022 14:05 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationLogPageQueryParam extends PageQueryParam {

    /**
     * user id
     */
    private Long userId;

    /**
     * search keyword
     */
    private String searchKey;

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * database name
     */
    private String databaseName;

    /**
     * schema name
     */
    private String schemaName;
}
