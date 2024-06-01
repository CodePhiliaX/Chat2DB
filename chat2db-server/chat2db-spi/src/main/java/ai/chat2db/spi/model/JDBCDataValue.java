package ai.chat2db.spi.model;

import ai.chat2db.spi.util.ResultSetUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;
import java.sql.*;

/**
 * @author: zgq
 * @date: 2024年05月30日 20:48
 */
@Data
@AllArgsConstructor
public class JDBCDataValue {
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int columnIndex;

    public Object getObject() {
        try {
            return resultSet.getObject(columnIndex);
        } catch (Exception e) {
            try {
                return resultSet.getString(columnIndex);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String getString() {
        return ResultSetUtils.getString(resultSet, columnIndex);
    }

    public String getType() {
        return ResultSetUtils.getColumnDataTypeName(metaData, columnIndex);
    }

    public InputStream getBinaryStream() {
        return ResultSetUtils.getBinaryStream(resultSet, columnIndex);
    }

    public int getPrecision() {
        return ResultSetUtils.getColumnPrecision(metaData, columnIndex);
    }

    public byte[] getBytes() {
        return ResultSetUtils.getBytes(resultSet, columnIndex);
    }

    public boolean getBoolean() {
        return ResultSetUtils.getBoolean(resultSet, columnIndex);
    }

    public int getScale() {
        return ResultSetUtils.getColumnScale(metaData, columnIndex);
    }

    public int getInt() {

        return ResultSetUtils.getInt(resultSet,columnIndex);
    }

    public Date getDate() {
        return ResultSetUtils.getDate(resultSet,columnIndex);
    }

    public Timestamp getTimestamp() {
        return ResultSetUtils.getTimestamp(resultSet,columnIndex);
    }
}
