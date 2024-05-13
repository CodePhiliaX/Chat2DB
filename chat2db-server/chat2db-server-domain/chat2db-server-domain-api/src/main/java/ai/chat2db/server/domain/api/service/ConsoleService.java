package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.ConsoleConnectParam;
import ai.chat2db.server.domain.api.param.ConsoleCloseParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;

/**
 * Data source management services
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 September 23, 2022 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface ConsoleService {

    /**
     * Create console link
     *
     * @param param
     * @return
     */
    ActionResult createConsole(ConsoleConnectParam param);

    /**
     * close connection
     *
     * @param param
     * @return
     */
    ActionResult closeConsole(ConsoleCloseParam param);

}
