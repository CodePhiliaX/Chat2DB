/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.postgresql.model;

import java.util.List;

import ai.chat2db.server.domain.support.model.TableIndex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : PostgresqlTableIndex.java, v 0.1 2022年12月11日 15:27 jipengfei Exp $
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostgresqlTableIndex extends TableIndex {
    private String tableSchemaName;
    private long oid;
    private String accessMethod;
    private String tableSpace;
    private int fillRate;
    private boolean unique;
    private boolean cluster;
    private boolean parallelBuild;
    private String constraint;
    private int fieldCount;
    private List<PostgresqlTableIndexField> fields;
}