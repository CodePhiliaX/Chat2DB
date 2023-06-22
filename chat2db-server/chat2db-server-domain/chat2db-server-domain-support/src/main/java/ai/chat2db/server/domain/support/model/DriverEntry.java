/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.model;

import java.sql.Driver;

import ai.chat2db.server.domain.support.enums.DbTypeEnum;
import ai.chat2db.server.domain.support.enums.DriverTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author jipengfei
 * @version : DriverEntry.java
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DriverEntry {

    private DriverTypeEnum driverTypeEnum;

    private Driver driver;

}