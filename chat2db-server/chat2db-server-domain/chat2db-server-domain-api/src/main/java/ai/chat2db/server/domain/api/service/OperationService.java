package ai.chat2db.server.domain.api.service;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.domain.api.model.Operation;
import ai.chat2db.server.domain.api.param.OperationPageQueryParam;
import ai.chat2db.server.domain.api.param.OperationSavedParam;
import ai.chat2db.server.domain.api.param.OperationUpdateParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.base.wrapper.result.PageResult;

/**
 * 用户保存ddl
 *
 * @author moji
 * @version UserSavedDdlCoreService.java, v 0.1 2022年09月23日 17:35 moji Exp $
 * @date 2022/09/23
 */
public interface OperationService {

    /**
     * 保存用户的ddl
     *
     * @param param
     * @return
     */
    DataResult<Long> create(OperationSavedParam param);

    /**
     * 更新用户的ddl
     *
     * @param param
     * @return
     */
    ActionResult update(OperationUpdateParam param);

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    DataResult<Operation> find(@NotNull Long id);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    ActionResult delete(@NotNull Long id);

    /**
     * 查询用户执行的ddl记录
     *
     * @param param
     * @return
     */
    PageResult<Operation> queryPage(OperationPageQueryParam param);
}
