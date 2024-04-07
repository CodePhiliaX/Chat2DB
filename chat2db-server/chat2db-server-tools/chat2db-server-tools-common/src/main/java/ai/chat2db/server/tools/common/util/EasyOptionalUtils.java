package ai.chat2db.server.tools.common.util;

import java.util.Optional;
import java.util.function.Function;

/**
 * Optional tool class
 *
 * @author Jiaju Zhuang
 */
public class EasyOptionalUtils {

    /**
     * Get the value of an object that may not be null
     *
     * @param source original object
     * @param function conversion method
     * @param <T>
     * @param <R>
     * @return Return value If empty, return null
     */
    public static <T, R> R mapTo(T source, Function<T, R> function) {
        return mapTo(source, function, null);
    }

    /**
     * Get the value of an object that may not be null
     *
     * @param source original object
     * @param function conversion method
     * @param defaultValue default value
     * @param <T>
     * @param <R>
     * @return return value
     */
    public static <T, R> R mapTo(T source, Function<T, R> function, R defaultValue) {
        return Optional.ofNullable(source).map(function).orElse(defaultValue);
    }
}
