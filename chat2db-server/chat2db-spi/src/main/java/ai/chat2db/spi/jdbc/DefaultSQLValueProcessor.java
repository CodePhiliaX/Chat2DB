package ai.chat2db.spi.jdbc;

import ai.chat2db.spi.SQLValueProcessor;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author: zgq
 * @date: 2024年05月24日 14:30
 */
public class DefaultSQLValueProcessor implements SQLValueProcessor {
    /**
     * @param rs
     * @param index
     * @return
     */
    @Override
    public String getSqlValueString(ResultSet rs, int index) throws SQLException {
        Object object = rs.getObject(index);
        if (Objects.isNull(object)) {
            return "NULL";
        }
        if (object instanceof BigDecimal bigDecimal) {
            return bigDecimal.toPlainString();
        } else if (object instanceof Float f) {
            return BigDecimal.valueOf(f).toPlainString();
        } else if (object instanceof Double d) {
            return BigDecimal.valueOf(d).toPlainString();
        } else if (object instanceof Number n) {
            return n.toString();
        } else if (object instanceof Boolean) {
            return (Boolean) object ? "1" : "0";
        } else if (object instanceof byte[]) {
            return converterByteArray2Str((byte[]) object);
        } else if (object instanceof Blob B) {
            return converterByteArray2Str(B.getBytes(1, Math.toIntExact(B.length())));
        } else if (object instanceof Clob c) {
            return converterClob2Str(c);
        }
        return "'" + escapeString(object) + "'";
    }

    private String escapeString(Object object) {
        String s = (String) object;
        if (StringUtils.isBlank(s)) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("'", "''");
    }

    private String converterClob2Str(Clob c) {
        StringBuilder stringBuilder = new StringBuilder();
        try (Reader reader = c.getCharacterStream()) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return escapeString(stringBuilder.toString());
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String converterByteArray2Str(byte[] bytes) {
        return "0x" + BaseEncoding.base16().encode(bytes);
    }
}
