package ai.chat2db.spi.jdbc;

import ai.chat2db.server.tools.common.util.I18nUtils;
import ai.chat2db.spi.ValueHandler;
import cn.hutool.core.io.unit.DataSizeUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class DefaultValueHandler implements ValueHandler {

    private static final long MAX_RESULT_SIZE = 256 * 1024;

    @Override
    public String getString(ResultSet rs, int index, boolean limitSize) throws SQLException {
        Object obj = rs.getObject(index);
        if (obj == null) {
            return null;
        }
        try {
            if (obj instanceof BigDecimal bigDecimal) {
                return bigDecimal.toPlainString();
            } else if (obj instanceof Double d) {
                return BigDecimal.valueOf(d).toPlainString();
            } else if (obj instanceof Float f) {
                return BigDecimal.valueOf(f).toPlainString();
            } else if (obj instanceof Clob) {
                return largeString(rs, index, limitSize);
            } else if (obj instanceof byte[]) {
                return largeString(rs, index, limitSize);
            } else if (obj instanceof Blob blob) {
                return largeStringBlob(blob, limitSize);
            }
            return obj.toString();
        } catch (Exception e) {
            log.warn("解析数失败:{},{}", index, obj, e);
            return obj.toString();
        }
    }

    private String largeStringBlob(Blob blob, boolean limitSize) throws SQLException {
        if (blob == null) {
            return null;
        }
        int length = Math.toIntExact(blob.length());
        if (limitSize && length > MAX_RESULT_SIZE) {
            length = Math.toIntExact(MAX_RESULT_SIZE);
        }
        byte[] data = blob.getBytes(1, length);
        String result = new String(data);

        if (length > MAX_RESULT_SIZE) {
            return "[ " + DataSizeUtil.format(MAX_RESULT_SIZE) + " of " + DataSizeUtil.format(length)
                    + " ,"
                    + I18nUtils.getMessage("execute.exportCsv") + " ] " + result;
        }
        return result;
    }

    private static String largeString(ResultSet rs, int index, boolean limitSize) throws SQLException {
        String result = rs.getString(index);
        if (result == null) {
            return null;

        }
        if (!limitSize) {
            return result;
        }

        if (result.length() > MAX_RESULT_SIZE) {
            return "[ " + DataSizeUtil.format(MAX_RESULT_SIZE) + " of " + DataSizeUtil.format(result.length()) + " ,"
                    + I18nUtils.getMessage("execute.exportCsv") + " ] " + result.substring(0,
                    Math.toIntExact(MAX_RESULT_SIZE));
        }
        return result;
    }
}
