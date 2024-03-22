package ai.chat2db.server.domain.api.param.datasource.access;

import java.util.List;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
@Data
public class DataSourceAccessBatchCreatParam extends PageQueryParam {
    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DataSource Access Object
     */
    @NotNull
    @NotEmpty
    private List<DataSourceAccessObjectParam> accessObjectList;
}
