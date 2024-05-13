package ai.chat2db.server.tools.common.util;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import ai.chat2db.server.tools.base.enums.BaseEnum;

/**
 * enum tool class
 * <p>
 * Mainly to solve the problem of each enumeration,
 * you need to write a function to get the value according to the code,
 * which does not seem very friendly.
 *
 * @author Jiaju Zhuang
 */
public class EasyEnumUtils {
    /**
     * Enumeration cache does not need to loop to read the enumeration every time
     */
    private static final Map<String, Map<?, BaseEnum<?>>> ENUM_CACHE = new ConcurrentHashMap<>();

    /**
     * Get the description of the enumeration based on an enumeration type
     *
     * @param clazz enumeration class
     * @param code Enumeration encoding
     * @param <T> The type of enumeration
     * @return If the code cannot be found, the return value is empty.
     */
    public static <T extends BaseEnum<?>> String getDescription(final Class<T> clazz, final String code) {
        BaseEnum<?> baseEnum = getEnum(clazz, code);
        if (baseEnum == null) {
            return null;
        }
        return baseEnum.getDescription();
    }

    /**
     * Get the description of the enumeration based on an enumeration type
     *
     * @param clazz enumeration class
     * @param code Enumeration encoding
     * @param <T> The type of enumeration
     * @return If the code cannot be found, the return value is empty.
     */
    public static <T extends BaseEnum<?>> T getEnum(final Class<T> clazz, final String code) {
        return getEnumMap(clazz).get(code);
    }

    /**
     * Verify whether it is a valid enumeration
     *
     * @param clazz enumeration class
     * @param code the encoding of the enumeration, null is also considered a valid enumeration
     * @param <T> The type of enumeration
     * @return Is it valid?
     */
    public static <T extends BaseEnum<?>> boolean isValidEnum(final Class<T> clazz, final String code) {
        return isValidEnum(clazz, code, true);
    }

    /**
     * Verify whether it is a valid enumeration
     *
     * @param clazz enumeration class
     * @param code The encoding of the enumeration. If it is empty, it is considered an invalid enumeration.
     * @param ignoreNull whether to ignore empty codes
     * @param <T> The type of enumeration
     * @return Is it valid?
     */
    public static <T extends BaseEnum<?>> boolean isValidEnum(final Class<T> clazz, final String code,
        final boolean ignoreNull) {
        if (code == null) {
            return ignoreNull;
        }
        return getEnumMap(clazz).containsKey(code);
    }

    /**
     * Get the map of an enumerated code Enum
     *
     * @param clazz enumeration class
     * @param <T> The type of enumeration
     * @return Map<code, Enum>
     */
    public static <T extends BaseEnum<?>> Map<String, T> getEnumMap(final Class<T> clazz) {
        String className = clazz.getName();
        Map<?, BaseEnum<?>> result = ENUM_CACHE.computeIfAbsent(className, value -> {
            T[] baseEnums = clazz.getEnumConstants();
            return Arrays.stream(baseEnums)
                .collect(Collectors.toMap(BaseEnum::getCode, Function.identity()));
        });
        return (Map)result;
    }
}
