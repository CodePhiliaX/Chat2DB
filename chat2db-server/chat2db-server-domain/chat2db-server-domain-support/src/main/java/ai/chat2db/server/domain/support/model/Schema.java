/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : TableSchema.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Schema {
    /**
     * 数据名字
     */
    private String name;
}