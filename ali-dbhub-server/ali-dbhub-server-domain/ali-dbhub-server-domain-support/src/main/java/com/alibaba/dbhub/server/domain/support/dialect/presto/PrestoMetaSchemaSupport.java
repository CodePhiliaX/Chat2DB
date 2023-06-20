/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.presto;

import javax.validation.constraints.NotEmpty;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;

/**
 * @author jipengfei
 * @version : PrestoMetaSchemaSupport.java
 */
public class PrestoMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {

    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.PRESTO;
    }
    @Override
    public String tableDDL(@NotEmpty String databaseName, String schemaName, @NotEmpty String tableName) {
       return "";
    }
}