
package ai.chat2db.server.domain.api.service;

import jakarta.validation.constraints.NotNull;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;

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
    void create(SystemConfigParam param);

    /**
     * 修改配置
     *
     * @param param
     * @return
     */
    void update(SystemConfigParam param);

    /**
     * 插入或者更新
     * @param param
     * @return
     */
    void createOrUpdate(SystemConfigParam param);

    /**
     * 根据code查询
     *
     * @param code
     * @return
     */
    Config find(@NotNull String code);

    /**
     * 删除
     *
     * @param code
     * @return
     */
    void delete(@NotNull String code);
}