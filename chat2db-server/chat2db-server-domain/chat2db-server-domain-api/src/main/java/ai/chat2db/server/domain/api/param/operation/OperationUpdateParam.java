package ai.chat2db.server.domain.api.param.operation;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version UserSavedDdlCreateParam.java, v 0.1 2022年09月25日 15:40 moji Exp $
 * @date 2022/09/25
 */
@Data
public class OperationUpdateParam {

    /**
     * 主键
     */
    @NotNull
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
     * 表所在空间
     */
    private String schemaName;

    /**
     * 保存名称
     */
    private String name;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * ddl语句状态:DRAFT/RELEASE
     */
    private String status;

    /**
     * ddl内容
     */
    private String ddl;

    /**
     * 是否在tab中被打开,y表示打开,n表示未打开
     */
    private String tabOpened;

    /**
     * operation type
     */
    private String operationType;
}
