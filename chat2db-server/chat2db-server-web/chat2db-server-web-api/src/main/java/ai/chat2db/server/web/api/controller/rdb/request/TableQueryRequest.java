
package ai.chat2db.server.web.api.controller.rdb.request;

import java.io.Serial;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.tools.base.wrapper.request.PageQueryRequest;
import ai.chat2db.server.web.api.controller.data.source.request.DataSourceBaseRequestInfo;

import lombok.Data;

/**
 * @author jipengfei
 * @version : TableColumnQueryRequest.java
 */
@Data
public class TableQueryRequest extends PageQueryRequest implements DataSourceBaseRequestInfo {

    @Serial
    private static final long serialVersionUID = 5794716286491282784L;

    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB名称
     */
    @NotNull
    private String databaseName;

    /**
     * 表名
     */
    private String tableName;
}