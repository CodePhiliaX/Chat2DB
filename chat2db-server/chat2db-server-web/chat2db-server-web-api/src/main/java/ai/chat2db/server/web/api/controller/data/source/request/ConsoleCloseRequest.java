package ai.chat2db.server.web.api.controller.data.source.request;

import lombok.Data;

/**
 * @author moji
 * @version ConsoleContentRequest.java, v 0.1 October 30, 2022 15:52 moji Exp $
 * @date 2022/10/30
 */
@Data
public class ConsoleCloseRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo{

    /**
     * console id
     */
    private Long consoleId;
}
