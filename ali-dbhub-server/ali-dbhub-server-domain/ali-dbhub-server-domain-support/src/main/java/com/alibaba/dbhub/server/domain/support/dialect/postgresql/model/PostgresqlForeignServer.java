/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.postgresql.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : PostgresqlForeignServer.java, v 0.1 2022年12月11日 16:23 jipengfei Exp $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostgresqlForeignServer {
    private String serverName;
    private String serverCatalog;
}