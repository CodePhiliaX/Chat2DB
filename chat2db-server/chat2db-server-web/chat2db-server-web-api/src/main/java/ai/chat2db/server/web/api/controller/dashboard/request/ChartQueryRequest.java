package ai.chat2db.server.web.api.controller.dashboard.request;

import java.util.List;

import lombok.Data;

/**
 * @author moji
 * @version ChartQueryRequest.java, v 0.1 June 9, 2023 17:46 moji Exp $
 * @date 2023/06/09
 */
@Data
public class ChartQueryRequest {

    /**
     * Chart ID list
     */
    private List<Long> ids;
}
