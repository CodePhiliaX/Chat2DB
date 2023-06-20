package com.alibaba.dbhub.server.web.api.controller.ai.converter;

import com.alibaba.dbhub.server.domain.api.param.TableQueryParam;
import com.alibaba.dbhub.server.web.api.controller.ai.request.ChatQueryRequest;

import org.mapstruct.Mapper;

/**
 * @author moji
 * @version ChatConverter.java, v 0.1 2023年04月02日 13:31 moji Exp $
 * @date 2023/04/02
 */
@Mapper(componentModel = "spring")
public abstract class ChatConverter {

    /**
     * 参数转换
     *
     * @param request
     * @return
     */
    public abstract TableQueryParam chat2tableQuery(ChatQueryRequest request);
}
