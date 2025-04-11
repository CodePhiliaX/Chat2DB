package ai.chat2db.server.web.api.controller.rdb.request;


import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;

/**
 * Query sequence brief request
 *
 * @author Sylphy
 */
@Data
public class SequenceBriefQueryRequest extends PageQueryRequest implements DataSourceBaseRequestInfo {

    @Serial
    private static final long serialVersionUID = -1324577112324436332L;

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
     * The space where the sequence is located is required by pg and oracle, but not by mysql.
     */
    private String schemaName;
}
