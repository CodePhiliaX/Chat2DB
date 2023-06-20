/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.web.api.controller.rdb.request;

import com.alibaba.dbhub.server.web.api.controller.data.source.request.DataSourceBaseRequest;

import lombok.Data;

/**
 * @author jipengfei
 * @version : UpdateSchemaRequest.java
 */
@Data
public class UpdateSchemaRequest extends DataSourceBaseRequest {

    private String newSchemaName;

}