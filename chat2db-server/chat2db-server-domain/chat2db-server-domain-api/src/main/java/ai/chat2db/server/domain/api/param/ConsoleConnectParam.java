package ai.chat2db.server.domain.api.param;

import lombok.Data;

/**
 * @author moji
 * @version ConsoleConnectParam.java, v 0.1 October 30, 2022 15:53 moji Exp $
 * @date 2022/10/30
 */
@Data
public class ConsoleConnectParam {

    /**
     * Data source id
     */
    private Long dataSourceId;

    /**
     * databaseName
     */
    private String databaseName;

    /**
     * console id
     */
    private Long consoleId;
}
