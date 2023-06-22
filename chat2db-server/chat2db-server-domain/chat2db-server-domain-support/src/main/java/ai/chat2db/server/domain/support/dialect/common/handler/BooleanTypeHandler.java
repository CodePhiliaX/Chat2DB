/**
 * alibaba.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.domain.support.dialect.common.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ai.chat2db.server.tools.base.enums.YesOrNoEnum;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author jipengfei
 * @version : NullableTypeHandler.java
 */
public class BooleanTypeHandler implements TypeHandler<Boolean> {

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
        if (YesOrNoEnum.YES.getCode().equalsIgnoreCase(result)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}