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
     * primary key
     */
    @NonNull
    private Long id;

    /**
     * user id
     */
    @NonNull
    private Long userId;

}
