
package ai.chat2db.server.admin.api.controller.user.vo;

import ai.chat2db.server.admin.api.controller.datasource.vo.SimpleDataSourceVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Pagination query
 *
 * @author Jiaju Zhuang
 */
@Data
public class UserDataSourcePageQueryVO {

    /**
     * 主键
     */
    @NotNull
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * Data Source
     */
    private SimpleDataSourceVO dataSource;
}
