package com.alibaba.dbhub.server.web.api.controller.rdb.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author moji
 * @version TableCreateDdlQueryRequest.java, v 0.1 2022年09月16日 14:23 moji Exp $
 * @date 2022/09/16
 */
@Data
public class TableCreateDdlQueryRequest {

    /**
     * DB类型
     * @see com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum
     */
    @NotNull
    private String dbType;


}
