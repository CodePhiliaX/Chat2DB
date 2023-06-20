/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.oceanbase;

import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.mysql.MysqlMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;

/**
 * @author jipengfei
 * @version : OceanBaseMetaSchemaSupport.java
 */
public class OceanBaseMetaSchemaSupport extends MysqlMetaSchemaSupport implements MetaSchema {
    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.OCEANBASE;
    }
}