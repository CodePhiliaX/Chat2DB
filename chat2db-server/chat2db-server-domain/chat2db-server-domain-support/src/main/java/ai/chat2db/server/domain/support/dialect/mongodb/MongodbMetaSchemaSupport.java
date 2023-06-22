/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.mongodb;

import ai.chat2db.server.domain.support.dialect.BaseMetaSchema;
import ai.chat2db.server.domain.support.dialect.MetaSchema;
import ai.chat2db.server.domain.support.enums.DbTypeEnum;

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