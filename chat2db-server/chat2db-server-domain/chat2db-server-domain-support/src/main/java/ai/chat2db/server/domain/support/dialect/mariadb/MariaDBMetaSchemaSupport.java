/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.mariadb;

import ai.chat2db.server.domain.support.dialect.BaseMetaSchema;
import ai.chat2db.server.domain.support.dialect.MetaSchema;
import ai.chat2db.server.domain.support.dialect.mysql.MysqlMetaSchemaSupport;
import ai.chat2db.server.domain.support.enums.DbTypeEnum;

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