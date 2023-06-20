/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.mysql.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * ColumnKey专用的转换器
 *
 * @author 是仪
 */
public class MysqlColumnKeyHandler implements TypeHandler<Boolean> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
    }

    @Override
    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));

    }

    @Override
    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private Boolean parse(String result) {
        if ("PRI".equalsIgnoreCase(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}