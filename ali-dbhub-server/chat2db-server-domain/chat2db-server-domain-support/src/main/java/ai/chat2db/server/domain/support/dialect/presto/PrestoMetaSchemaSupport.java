/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.presto;

import javax.validation.constraints.NotEmpty;

import ai.chat2db.server.domain.support.dialect.BaseMetaSchema;
import ai.chat2db.server.domain.support.dialect.MetaSchema;
import ai.chat2db.server.domain.support.enums.DbTypeEnum;

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