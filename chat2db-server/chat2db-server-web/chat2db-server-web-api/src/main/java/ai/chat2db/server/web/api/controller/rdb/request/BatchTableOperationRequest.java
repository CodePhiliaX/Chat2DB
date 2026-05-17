package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量表操作请求（OPTIMIZE/ANALYZE）
 */
@Data
public class BatchTableOperationRequest extends DataSourceBaseRequest {

    /**
     * 表名列表
     */
    @NotEmpty
    private List<String> tableNames;
}
