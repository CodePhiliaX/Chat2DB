
package ai.chat2db.spi.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.google.common.collect.Lists;


/**
 * @author jipengfei
 * @version : ResultSetUtils.java
 */
public class ResultSetUtils {



    private static List<String> getRsHeader(ResultSet rs) {
        try {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int col = resultSetMetaData.getColumnCount();
            List<String> headerList = Lists.newArrayListWithExpectedSize(col);
            for (int i = 1; i <= col; i++) {
                headerList.add(getColumnName(resultSetMetaData, i));
            }
            return headerList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param rs
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> List<T> toObjectList(ResultSet rs, Class<T> clazz) {
        try {
            if (rs == null || clazz == null) {
                return Lists.newArrayList();
            }
            List<T> list = Lists.newArrayList();
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int col = rsMetaData.getColumnCount();
            List<String> headerList = getRsHeader(rs);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= col; i++) {
                    map.put(headerList.get(i-1), rs.getObject(i));
                }
                T obj = mapper.convertValue(map, clazz);

                list.add(obj);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getColumnName(ResultSetMetaData resultSetMetaData, int column) throws SQLException {
        String columnLabel = resultSetMetaData.getColumnLabel(column);
        if (columnLabel != null) {
            return columnLabel;
        }
        return resultSetMetaData.getColumnName(column);
    }
}