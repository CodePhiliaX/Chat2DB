package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;

import lombok.Data;

/**
 * @author moji
 * @version TableManageRequest.java, v 0.1 September 16, 2022 17:55 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DdlRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

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

    /**
     * Page coding
     * Only available for select statements
     */
    private Integer pageNo;

    /**
     * Paging Size
     * Only available for select statements
     */
    private Integer pageSize;

    /**
     * Return all data
     * Only available for select statements
     */
    private Boolean pageSizeAll;
}
