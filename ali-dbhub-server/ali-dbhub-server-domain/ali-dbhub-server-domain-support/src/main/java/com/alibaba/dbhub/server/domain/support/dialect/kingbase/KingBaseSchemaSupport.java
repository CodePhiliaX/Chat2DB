/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.kingbase;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;

/**
 * @author jipengfei
 * @version : H2MetaSchemaSupport.java
 */
public class KingBaseSchemaSupport extends BaseMetaSchema implements MetaSchema {
    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.KINGBASE;
    }
}