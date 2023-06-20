/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alibaba.dbhub.server.domain.support.dialect.h2.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.dbhub.server.domain.support.dialect.h2.H2IndexTypeEnum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author jipengfei
 * @version : IndexTypeHandler.java, v 0.1 2022年12月15日 10:36 jipengfei Exp $
 */
public class H2IndexTypeHandler implements TypeHandler<String> {
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));

    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }


    private String parse(String result) {

        if (H2IndexTypeEnum.PRIMARY_KEY.getCode().equalsIgnoreCase(result)) {
            return H2IndexTypeEnum.PRIMARY_KEY.getIndexType().getCode();
        } else if (H2IndexTypeEnum.UNIQUE.getCode().equalsIgnoreCase(result)) {
            return H2IndexTypeEnum.UNIQUE.getIndexType().getCode();
        } else {
            return H2IndexTypeEnum.NORMAL.getIndexType().getCode();
        }
    }
}