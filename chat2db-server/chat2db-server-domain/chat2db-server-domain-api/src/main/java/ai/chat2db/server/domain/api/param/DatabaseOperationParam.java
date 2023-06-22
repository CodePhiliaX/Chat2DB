/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.api.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jipengfei
 * @version : DatabaseOperationParam.java
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class DatabaseOperationParam {

    private String databaseName;

    private String newDatabaseName;
}