package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.param.operation.OperationLogPageQueryParam;
import ai.chat2db.server.domain.api.model.OperationLog;
import ai.chat2db.server.domain.api.param.operation.OperationLogCreateParam;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

/**
 * 用户执行ddl
 *
 * @author moji
 * @version UserExecutedDdlCoreService.java, v 0.1 2022年09月23日 17:35 moji Exp $
 * @date 2022/09/23
 */
public interface OperationLogService {

    /**
     * 创建用户执行的ddl记录
     *
     * @param param
     * @return
     */
    DataResult<Long> create(OperationLogCreateParam param);

    /**
     * 查询用户执行的ddl记录
     *
     * @param param
     * @return
     */
    PageResult<OperationLog> queryPage(OperationLogPageQueryParam param);
}
