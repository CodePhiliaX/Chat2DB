package ai.chat2db.server.web.api.controller.rdb.request;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;

import lombok.Data;

/**
 * @author moji
 * @version TableManageRequest.java, v 0.1 2022年09月16日 17:55 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DdlRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

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

    /**
     * 分页编码
     * 只有select语句才有
     */
    private Integer pageNo;

    /**
     * 分页大小
     * 只有select语句才有
     */
    private Integer pageSize;

    /**
     * 返回全部数据
     * 只有select语句才有
     */
    private Boolean pageSizeAll;
}
