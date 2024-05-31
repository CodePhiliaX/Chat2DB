package ai.chat2db.spi.sql;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

/**
 * @author luojun
 * @version 1.0
 * @description: 接口定义
 * @date 2024/5/31 19:05
 **/
public class DocumentUtils {

    public static LinkedHashMap<String, Object> convertToMap(Object obj) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (obj != null) {
            String json = JSON.toJSONString(obj);
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject m = jsonObject.getJSONObject("documentAsMap");
            if (m != null) {
                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return map;
    }
}
