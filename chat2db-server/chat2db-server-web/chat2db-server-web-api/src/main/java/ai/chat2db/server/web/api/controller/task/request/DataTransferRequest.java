package ai.chat2db.server.web.api.controller.task.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 数据传输请求参数
 */
@Data
public class DataTransferRequest {

    @NotNull
    private Long sourceDataSourceId;

    private String sourceDatabaseName;

    private String sourceSchemaName;

    @NotNull
    private Long targetDataSourceId;

    private String targetDatabaseName;

    private String targetSchemaName;

    @NotEmpty
    private List<String> tableNames;
}
