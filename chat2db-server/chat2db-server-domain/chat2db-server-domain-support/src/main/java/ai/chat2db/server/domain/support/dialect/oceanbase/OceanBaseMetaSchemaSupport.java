/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.oceanbase;

import ai.chat2db.server.domain.support.dialect.MetaSchema;
import ai.chat2db.server.domain.support.dialect.mysql.MysqlMetaSchemaSupport;
import ai.chat2db.server.domain.support.enums.DbTypeEnum;

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