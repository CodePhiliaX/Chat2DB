/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.api.param;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : SchemaQueryParam.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaQueryParam {

    @NotNull
    private Long dataSourceId;

    @NotNull
    private String dataBaseName;
}