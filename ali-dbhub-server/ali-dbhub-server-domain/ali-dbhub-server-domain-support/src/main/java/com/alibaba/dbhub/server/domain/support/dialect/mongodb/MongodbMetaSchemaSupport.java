/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.mongodb;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;

/**
 * @author jipengfei
 * @version : MongodbMetaSchemaSupport.java
 */
public class MongodbMetaSchemaSupport extends BaseMetaSchema implements MetaSchema {
    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.MONGODB;
    }
}