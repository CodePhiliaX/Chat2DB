package ai.chat2db.server.domain.api.service;

import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.operation.OperationPageQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationQueryParam;
import ai.chat2db.server.domain.api.param.operation.OperationSavedParam;
import ai.chat2db.server.domain.api.param.operation.OperationUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;
import jakarta.validation.constraints.NotNull;

/**
 * user save ddl
 *
 * @author moji
 * @version UserSavedDdlCoreService.java, v 0.1 September 23, 2022 17:35 moji Exp $
 * @date 2022/09/23
 */
public interface OperationService {

    /**
     * Save user's ddl
     *
     * @param param
     * @return
     */
    DataResult<Long> createWithPermission(OperationSavedParam param);

    /**
     * Update user's ddl
     *
     * @param param
     * @return
     */
    ActionResult updateWithPermission(OperationUpdateParam param);

    /**
     * Query based on id
     *
     * @param id
     * @return
     */
    DataResult<Operation> find(@NotNull Long id);

    /**
     * Query based on id
     *
     * @param id
     * @return
     */
    DataResult<Operation> queryExistent(@NotNull Long id);
    /**
     * Query a piece of data
     *
     * @param param
     * @return
     */
    DataResult<Operation> queryExistent(@NotNull OperationQueryParam param);
    /**
     * delete
     *
     * @param id
     * @return
     */
    ActionResult deleteWithPermission(@NotNull Long id);

    /**
     * Query the ddl records executed by the user
     *
     * @param param
     * @return
     */
    PageResult<Operation> queryPage(OperationPageQueryParam param);
}
