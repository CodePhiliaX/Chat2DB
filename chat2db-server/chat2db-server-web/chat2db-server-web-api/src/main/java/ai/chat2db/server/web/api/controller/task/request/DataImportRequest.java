package ai.chat2db.server.web.api.controller.task.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据导入请求参数
 */
@Data
public class DataImportRequest extends DataSourceBaseRequest {

    /**
     * 目标表名
     */
    @NotBlank
    private String tableName;

    /**
     * 文件类型：CSV, XLSX, XLS
     */
    @NotNull
    private String fileType;

    /**
     * 数据库名
     */
    private String databaseName;

    /**
     * schema 名
     */
    private String schemaName;

    /**
     * 字段映射配置（JSON格式）
     * 格式：[{"sourceField":"源字段","targetField":"目标字段","primaryKey":false}]
     */
    private String fieldMappings;

    /**
     * 导入模式：INSERT/UPDATE/UPSERT/INSERT_IGNORE/DELETE/REPLACE
     */
    private String importMode;
}
