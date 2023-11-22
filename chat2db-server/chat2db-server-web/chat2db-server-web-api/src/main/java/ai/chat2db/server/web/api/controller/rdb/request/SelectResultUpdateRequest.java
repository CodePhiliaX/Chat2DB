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
     * 展示头的列表
     */
    private List<Header> headerList;

    /**
     * 修改后数据的列表
     */
    @NotEmpty
    private List<SelectResultOperation> operations;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 控制台id
     */
    @NotNull
    private Long consoleId;
    @Override
    public Long getConsoleId() {
        return consoleId;
    }

}
