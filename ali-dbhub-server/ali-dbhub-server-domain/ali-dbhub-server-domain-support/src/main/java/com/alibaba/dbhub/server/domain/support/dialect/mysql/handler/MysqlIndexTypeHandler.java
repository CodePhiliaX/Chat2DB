/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.mysql.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.enums.IndexTypeEnum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author jipengfei
 * @version : IndexTypeHandler.java, v 0.1 2022年12月15日 10:36 jipengfei Exp $
 */
public class MysqlIndexTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName), rs.getString("INDEX_NAME"));
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex), rs.getString("INDEX_NAME"));
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex), cs.getString("INDEX_NAME"));
    }

    private String parse(String result, String indexName) {
        if ("PRIMARY".equalsIgnoreCase(indexName)) {
            return IndexTypeEnum.PRIMARY_KEY.getCode();
        } else {
            if ("1".equalsIgnoreCase(result)) {
                return IndexTypeEnum.NORMAL.getCode();
            } else {
                return IndexTypeEnum.UNIQUE.getCode();
            }
        }
    }
}