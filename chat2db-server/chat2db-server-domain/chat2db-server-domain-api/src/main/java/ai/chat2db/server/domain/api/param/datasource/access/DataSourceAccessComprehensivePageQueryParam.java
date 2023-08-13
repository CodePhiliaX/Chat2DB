package ai.chat2db.server.domain.api.param.datasource.access;

import ai.chat2db.server.domain.api.enums.AccessObjectTypeEnum;
import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;
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
    private Long dataSourceId;

    /**
     * 授权类型
     *
     * @see AccessObjectTypeEnum
     */
    private String accessObjectType;

    /**
     * 授权id,根据类型区分是用户还是团队
     */
    private Long accessObjectId;

    /**
     * Query keywords for users or teams
     */
    private String userOrTeamSearchKey;

    /**
     * Query keywords for data source
     */
    private String dataSourceSearchKey;
}
