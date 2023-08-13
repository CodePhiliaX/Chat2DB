package ai.chat2db.server.domain.api.param.datasource.access;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Source Access
 *
 * @author Jiaju Zhuang
 */
@Data
public class DataSourceAccessComprehensivePageQueryParam extends PageQueryParam {
    /**
     * 数据源id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * Query keywords for users or teams
     */
    private String userOrTeamSearchKey;

    /**
     * Query keywords for data source
     */
    private String dataSourceSearchKey;
}
