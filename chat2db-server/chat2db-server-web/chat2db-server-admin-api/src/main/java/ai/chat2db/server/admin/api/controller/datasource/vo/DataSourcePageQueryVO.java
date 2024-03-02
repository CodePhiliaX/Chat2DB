
package ai.chat2db.server.admin.api.controller.datasource.vo;

import ai.chat2db.server.common.api.controller.vo.SimpleEnvironmentVO;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class DataSourcePageQueryVO {

    /**
     * primary key id
     */
    private Long id;

    /**
     * Connection alias
     */
    private String alias;

    /**
     * connection address
     */
    private String url;

    /**
     * environment id
     */
    private Long environmentId;

    /**
     * environment
     */
    private SimpleEnvironmentVO environment;
}
