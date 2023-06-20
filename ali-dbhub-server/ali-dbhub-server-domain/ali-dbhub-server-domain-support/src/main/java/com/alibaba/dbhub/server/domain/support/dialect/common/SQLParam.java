/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : SQLParam.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SQLParam {
    private String databaseName;
    private String newDatabaseName;
    private String schemaName;
    private String newSchemaName;

}