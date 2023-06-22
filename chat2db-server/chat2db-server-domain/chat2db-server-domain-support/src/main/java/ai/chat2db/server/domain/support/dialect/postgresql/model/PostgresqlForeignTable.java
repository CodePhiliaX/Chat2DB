/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.postgresql.model;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : PostgresqlForeignTable.java, v 0.1 2022年12月11日 16:19 jipengfei Exp $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostgresqlForeignTable extends PostgresqlTable{
    private PostgresqlForeignServer server;
    private Map<String, String> foreignTableOptions = new LinkedHashMap();
}