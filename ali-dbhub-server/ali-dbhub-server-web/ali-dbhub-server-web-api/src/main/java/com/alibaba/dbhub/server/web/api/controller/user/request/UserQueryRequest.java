/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.controller.user.request;

import java.io.Serial;

import com.alibaba.dbhub.server.tools.base.wrapper.param.PageQueryParam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : UserQueyRequest.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest extends PageQueryParam {

    @Serial
    private static final long serialVersionUID = 5663790872812326134L;
    /**
     * 用户名魔化搜索
     */
    private String keyWord;
}