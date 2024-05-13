package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.operation.OperationLogPageQueryParam;
import ai.chat2db.server.domain.api.model.OperationLog;
import ai.chat2db.server.domain.api.param.operation.OperationLogCreateParam;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

/**
 * User executes ddl
 *
 * @author moji
 * @version UserExecutedDdlCoreService.java, v 0.1 September 23, 2022 17:35 moji Exp $
 * @date 2022/09/23
 */
public interface OperationLogService {

    /**
     * Create ddl record executed by user
     *
     * @param param
     * @return
     */
    DataResult<Long> create(OperationLogCreateParam param);

    /**
     * Query the ddl records executed by the user
     *
     * @param param
     * @return
     */
    PageResult<OperationLog> queryPage(OperationLogPageQueryParam param);
}
