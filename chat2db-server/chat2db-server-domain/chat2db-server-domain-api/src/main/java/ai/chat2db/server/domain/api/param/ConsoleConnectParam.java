package ai.chat2db.server.domain.api.param;

import lombok.Data;

/**
 * @author moji
 * @version ConsoleConnectParam.java, v 0.1 2022年10月30日 15:53 moji Exp $
 * @date 2022/10/30
 */
@Data
public class ConsoleConnectParam {

    /**
     * 数据源id
     */
    private Long dataSourceId;

    /**
     * 数据库db名称
     */
    private String databaseName;

    /**
     * 控制台id
     */
    private Long consoleId;
}
