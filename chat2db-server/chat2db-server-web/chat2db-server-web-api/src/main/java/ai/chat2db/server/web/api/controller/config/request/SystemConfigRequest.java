/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.web.api.controller.config.request;

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