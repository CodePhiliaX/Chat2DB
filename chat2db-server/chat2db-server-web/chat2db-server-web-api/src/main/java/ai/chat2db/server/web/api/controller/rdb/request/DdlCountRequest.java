package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;

import lombok.Data;

/**
 * 统计数量
 *
 * @author Jiaju Zhuang
 */
@Data
public class DdlCountRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    /**
     * sql语句
     */
    @NotNull
    private String sql;

    /**
     * 控制台id
     */
    @NotNull
    private Long consoleId;
}
