package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FunctionDetailRequest implements DataSourceBaseRequestInfo {

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;
    /**
     * DB name
     */
    private String databaseName;

    /**
     * The space where the table is located is required by pg and oracle, but not by mysql.
     */
    private String schemaName;

    /**
     * function name
     */
    private String functionName;

    /**
     * if true, refresh the cache
     */
    private boolean refresh;
}