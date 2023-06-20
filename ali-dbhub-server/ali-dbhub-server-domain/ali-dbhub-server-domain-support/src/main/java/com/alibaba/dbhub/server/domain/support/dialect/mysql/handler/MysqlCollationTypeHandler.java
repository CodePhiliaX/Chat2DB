/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.mysql.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.dialect.mysql.MysqlCollationEnum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 *
 * @author jipengfei
 * @version : MysqlCollationTypeHandler.java
 */
public class MysqlCollationTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        if (MysqlCollationEnum.DESC.getCode().equalsIgnoreCase(rs.getString(columnName))) {
            return MysqlCollationEnum.DESC.getCollation().getCode();
        } else {
            return MysqlCollationEnum.ASC.getCollation().getCode();
        }
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        if (MysqlCollationEnum.DESC.getCode().equalsIgnoreCase(rs.getString(columnIndex))) {
            return MysqlCollationEnum.DESC.getCollation().getCode();
        } else {
            return MysqlCollationEnum.ASC.getCollation().getCode();
        }
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (MysqlCollationEnum.DESC.getCode().equalsIgnoreCase(cs.getString(columnIndex))) {
            return MysqlCollationEnum.DESC.getCollation().getCode();
        } else {
            return MysqlCollationEnum.ASC.getCollation().getCode();
        }
    }
}