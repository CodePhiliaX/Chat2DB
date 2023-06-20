/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.api.service;

import javax.validation.constraints.NotNull;

import com.alibaba.dbhub.server.domain.api.model.Config;
import com.alibaba.dbhub.server.domain.api.param.SystemConfigParam;
import com.alibaba.dbhub.server.tools.base.wrapper.result.ActionResult;
import com.alibaba.dbhub.server.tools.base.wrapper.result.DataResult;

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