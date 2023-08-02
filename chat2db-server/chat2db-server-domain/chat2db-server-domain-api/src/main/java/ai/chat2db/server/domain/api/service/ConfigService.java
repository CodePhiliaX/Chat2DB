
package ai.chat2db.server.domain.api.service;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;

/**
 * @author jipengfei
 * @version : SystemConfigService.java
 */
public interface ConfigService {

    /**
     * 创建配置
     *
     * @param param
     * @return
     */
    ActionResult create(SystemConfigParam param);

    /**
     * 修改配置
     *
     * @param param
     * @return
     */
    ActionResult update(SystemConfigParam param);

    /**
     * 插入或者更新
     * @param param
     * @return
     */
    ActionResult createOrUpdate(SystemConfigParam param);

    /**
     * 根据code查询
     *
     * @param code
     * @return
     */
    DataResult<Config> find(@NotNull String code);

    /**
     * 删除
     *
     * @param code
     * @return
     */
    ActionResult delete(@NotNull String code);
}