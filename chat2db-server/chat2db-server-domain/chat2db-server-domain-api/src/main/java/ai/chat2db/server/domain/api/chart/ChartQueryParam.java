package ai.chat2db.server.domain.api.chart;

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
public class ChartQueryParam {

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
