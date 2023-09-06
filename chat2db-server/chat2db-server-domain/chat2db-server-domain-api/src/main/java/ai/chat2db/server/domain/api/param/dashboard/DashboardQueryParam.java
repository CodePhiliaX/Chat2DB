package ai.chat2db.server.domain.api.param.dashboard;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * query
 *
 * @author Jiaju Zhuang
 */
@Data
@NoArgsConstructor
public class DashboardQueryParam {

    /**
     * 主键
     */
    @NonNull
    private Long id;

    /**
     * 用户id
     */
    @NonNull
    private Long userId;

}
