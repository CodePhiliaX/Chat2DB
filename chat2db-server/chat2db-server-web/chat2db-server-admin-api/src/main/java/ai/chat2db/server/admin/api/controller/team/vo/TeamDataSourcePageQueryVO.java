
package ai.chat2db.server.admin.api.controller.team.vo;

import ai.chat2db.server.admin.api.controller.datasource.vo.SimpleDataSourceVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class TeamDataSourcePageQueryVO {

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * team id
     */
    private Long teamId;

    /**
     * Data Source
     */
    private SimpleDataSourceVO dataSource;
}
