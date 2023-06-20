package com.alibaba.dbhub.server.web.api.controller.ai.request;

import java.util.List;

import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * chat查询入参
 *
 * @author moji
 * @version ChatQueryRequest.java, v 0.1 2023年04月02日 13:28 moji Exp $
 * @date 2023/04/02
 */
@Data
public class ChatQueryRequest extends DataSourceBaseRequest {

    /**
     * 输入消息
     */
    private String message;

    /**
     * SQL功能类型
     * @see com.alibaba.dbhub.server.web.api.controller.ai.enums.PromptType
     */
    private String promptType;

    /**
     * 表名列表
     */
    private List<String> tableNames;

    /**
     * 目标SQL数据类型
     * @see com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum
     */
    private String destSqlType;

    /**
     * 更多备注信息：如要求或限制条件等
     */
    private String ext;
}
