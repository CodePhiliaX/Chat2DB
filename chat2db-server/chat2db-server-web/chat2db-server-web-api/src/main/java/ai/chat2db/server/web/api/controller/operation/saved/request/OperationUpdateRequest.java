package ai.chat2db.server.web.api.controller.operation.saved.request;

import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version DdlCreateRequest.java, v 0.1 2022年09月18日 11:13 moji Exp $
 * @date 2022/09/18
 */
@Data
public class OperationUpdateRequest {

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * 文件别名
     */
    private String name;

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
     * 数据库类型
     */
    private String type;

    /**
     * ddl内容
     */
    @NotNull
    private String ddl;

    /**
     * 更新状态 DRAFT/RELEASE
     */
    private String status;

    /**
     * 是否在tab中被打开,y表示打开,n表示未打开
     */
    private String tabOpened;

    /**
     * operation type
     */
    private String operationType;

    /**
     * 用户id
     */
    private Long userId;
}
