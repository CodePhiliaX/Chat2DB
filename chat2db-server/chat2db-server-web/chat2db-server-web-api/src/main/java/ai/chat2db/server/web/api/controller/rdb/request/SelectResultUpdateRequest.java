package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.domain.api.param.SelectResultOperation;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;
import ai.chat2db.spi.model.Header;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SelectResultUpdateRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    /**
     * List of display headers
     */
    private List<Header> headerList;

    /**
     * List of modified data
     */
    @NotEmpty
    private List<SelectResultOperation> operations;

    /**
     * Table Name
     */
    private String tableName;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;
    @Override
    public Long getConsoleId() {
        return consoleId;
    }

}
