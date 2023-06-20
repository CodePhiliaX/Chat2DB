/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.mariadb;

import com.alibaba.dbhub.server.domain.support.dialect.BaseMetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.MetaSchema;
import com.alibaba.dbhub.server.domain.support.dialect.mysql.MysqlMetaSchemaSupport;
import com.alibaba.dbhub.server.domain.support.enums.DbTypeEnum;

/**
 * @author jipengfei
 * @version : MariaDBMetaSchemaSupport.java
 */
public class MariaDBMetaSchemaSupport extends MysqlMetaSchemaSupport implements MetaSchema {
    @Override
    public DbTypeEnum dbType() {
        return DbTypeEnum.MARIADB;
    }
}