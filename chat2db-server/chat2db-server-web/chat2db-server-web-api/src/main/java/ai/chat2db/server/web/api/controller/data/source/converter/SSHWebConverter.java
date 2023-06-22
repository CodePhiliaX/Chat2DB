/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.web.api.controller.data.source.converter;

import ai.chat2db.server.domain.support.model.SSHInfo;
import ai.chat2db.server.web.api.controller.data.source.request.SSHTestRequest;

import org.mapstruct.Mapper;

/**
 * @author jipengfei
 * @version : SSHWebConverter.java
 */
@Mapper(componentModel = "spring")
public abstract class SSHWebConverter {

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract SSHInfo toInfo(SSHTestRequest request);
}