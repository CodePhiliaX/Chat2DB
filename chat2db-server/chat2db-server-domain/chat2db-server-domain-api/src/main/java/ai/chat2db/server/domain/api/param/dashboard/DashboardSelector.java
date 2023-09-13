package ai.chat2db.server.domain.api.param.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * selectro
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSelector {

    /**
     * 图表ID列表
     */
    private Boolean chartIds;
}
