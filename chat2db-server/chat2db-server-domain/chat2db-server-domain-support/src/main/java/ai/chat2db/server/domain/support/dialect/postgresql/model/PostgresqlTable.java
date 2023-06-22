/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.postgresql.model;

import ai.chat2db.server.domain.support.model.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : TableVO.java, v 0.1 2022年12月11日 14:34 jipengfei Exp $
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class PostgresqlTable extends Table {

    public boolean isForeignTable(){
        return false;
    }
}