/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.controller.config.request;

import lombok.Data;

/**
 * @author jipengfei
 * @version : SystemConfigRequest.java
 */
@Data
public class SystemConfigRequest {

    private String code;

    private String content;
}