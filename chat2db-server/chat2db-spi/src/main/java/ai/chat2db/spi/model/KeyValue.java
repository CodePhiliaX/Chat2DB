
package ai.chat2db.spi.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @author jipengfei
 * @version : KeyValue.java
 */
@Data
public class KeyValue implements Serializable {
    /**
     * attribute name
     */
    private String key;

    /**
     * attribute value
     */
    private String value;

    /**
     * Is it required?
     */
    private boolean required;

    /**
     * Options
     */
    private List<String> choices;

    public static Map<String, Object> toMap(List<KeyValue> keyValues) {
        if (CollectionUtils.isEmpty(keyValues)) {
            return Maps.newHashMap();
        } else {
            Map<String, Object> map = Maps.newHashMap();
            keyValues.forEach(keyValue -> map.put(keyValue.getKey(), String.valueOf(keyValue.getValue())));
            return map;
        }
    }
}