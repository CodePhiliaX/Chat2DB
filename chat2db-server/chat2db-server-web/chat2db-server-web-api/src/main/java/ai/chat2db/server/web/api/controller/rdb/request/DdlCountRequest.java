package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;

import lombok.Data;

/**
 * total number
 *
 * @author Jiaju Zhuang
 */
@Data
public class DdlCountRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    /**
     * sql statement
     */
    @NotNull
    private String sql;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;
}
