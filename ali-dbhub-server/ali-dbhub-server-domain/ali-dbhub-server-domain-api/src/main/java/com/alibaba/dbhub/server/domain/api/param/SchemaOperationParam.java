/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.api.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jipengfei
 * @version : SchemaOperationParam.java
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SchemaOperationParam {
    String databaseName;
    String schemaName;
    String newSchemaName;
}