
package ai.chat2db.server.admin.api.controller.datasource.vo;

import ai.chat2db.server.common.api.controller.vo.SimpleEnvironmentVO;
import lombok.Data;

/**
 * Data Source
 *
 * @author Jiaju Zhuang
 */
@Data
public class SimpleDataSourceVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 连接别名
     */
    private String alias;

    /**
     * 连接地址
     */
    private String url;

    /**
     * 环境id
     */
    private Long environmentId;

    /**
     * 环境
     */
    private SimpleEnvironmentVO environment;
}
