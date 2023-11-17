package ai.chat2db.spi.util;

import ai.chat2db.spi.config.DBConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class FileUtils {

    public static <T> T readJsonValue(Class<?> loaderClass, String path, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T value = null;
        try {
            value = mapper.readValue(loaderClass.getResourceAsStream(path), clazz);
            // 使用obj中的数据
        } catch (IOException e) {
            return null;
        }
        return value;
    }
}
