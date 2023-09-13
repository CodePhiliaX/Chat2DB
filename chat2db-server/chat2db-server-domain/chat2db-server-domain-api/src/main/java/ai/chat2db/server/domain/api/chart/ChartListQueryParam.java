package ai.chat2db.server.domain.api.chart;

import java.util.List;

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
public class ChartListQueryParam {

    /**
     * 主键
     */
    @NonNull
    private List<Long> idList;

    /**
     * 用户id
     */
    @NonNull
    private Long userId;

}
