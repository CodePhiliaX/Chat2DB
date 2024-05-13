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
     * primary key
     */
    @NonNull
    private List<Long> idList;

    /**
     * user id
     */
    @NonNull
    private Long userId;

}
