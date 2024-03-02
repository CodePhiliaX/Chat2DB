package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * Modify table sql request
 *
 * @author Shi Yi
 */
@Data
public class TableModifySqlRequest extends DataSourceBaseRequest {

    /**
     * Old table structure
     * Empty means creating a new table
     */
    private TableRequest oldTable;

    /**
     * new table structure
     */
    @NotNull
    private TableRequest newTable;

}
