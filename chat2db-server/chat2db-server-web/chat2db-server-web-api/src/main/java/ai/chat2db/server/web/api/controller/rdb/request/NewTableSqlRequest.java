package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewTableSqlRequest extends DataSourceBaseRequest {

    /**
     * new table structure
     */
    @NotNull
    private TableRequest newTable;

}
