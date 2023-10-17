package ai.chat2db.server.domain.api.chart;

import ai.chat2db.server.tools.base.wrapper.param.PageQueryParam;

import lombok.Data;

/**
 * @author moji
 * @version UserSavedDdlPageQueryParam.java, v 0.1 2022年09月25日 14:05 moji Exp $
 * @date 2022/09/25
 */
@Data
public class ChartPageQueryParam extends PageQueryParam {

    /**
     * 报表ID
     */
    private Long dashboardId;

    /**
     * 搜索关键词
     */
    private String searchKey;

}
