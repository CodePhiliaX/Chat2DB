package ai.chat2db.server.web.api.controller.pin.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PinTableRequest {

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB名称
     */
    private String databaseName;

    /**
     * 表所在空间
     */
    private String schemaName;
    /**
     * Pin table name
     */
    private String tableName;
}
