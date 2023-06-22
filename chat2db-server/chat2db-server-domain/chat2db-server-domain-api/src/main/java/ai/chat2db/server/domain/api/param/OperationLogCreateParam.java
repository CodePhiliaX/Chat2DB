package ai.chat2db.server.domain.api.param;

import lombok.Data;

/**
 * @author moji
 * @version UserExecutedDdlCreateParam.java, v 0.1 2022年09月25日 11:08 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationLogCreateParam {

    /**
     * 主键
     */
    private Long id;

    /**
     * 数据源连接ID
     */
    private Long dataSourceId;

    /**
     * db名称
     */
    private String databaseName;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * ddl内容
     */
    private String ddl;
}
