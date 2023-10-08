package ai.chat2db.server.web.api.controller.rdb.request;

import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceConsoleRequestInfo;
import ai.chat2db.server.web.api.controller.rdb.vo.HeaderVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SelectResultUpdateRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    /**
     * 展示头的列表
     */
    private List<HeaderVO> headerList;

    /**
     * 修改后数据的列表
     */
    private List<List<String>> dataList;

    /**
     * 数据的列表
     */
    private List<List<String>> oldDataList;

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
