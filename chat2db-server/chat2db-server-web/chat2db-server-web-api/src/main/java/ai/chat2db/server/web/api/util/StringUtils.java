package ai.chat2db.server.web.api.util;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Add string tool class. In order to be compatible with various JB products, try not to use third-party toolkits.
 *
 * @author lzy
 */
@SuppressWarnings("WeakerAccess")
public class StringUtils {

    private static final String EMPTY_STR = "null";

    /**
     * How to deal with initial letters
     */
    private static final BiFunction<String, Function<Integer, Integer>, String> FIRST_CHAR_HANDLER_FUN = (str, firstCharFun) -> {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = firstCharFun.apply(firstCodepoint);
        if (firstCodepoint == newCodePoint) {
            // already capitalized
            return str;
        }

        // cannot be longer than the char array
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        // copy the first codepoint
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            // copy the remaining ones
            newCodePoints[outOffset++] = codepoint;
            inOffset += Character.charCount(codepoint);
        }
        return new String(newCodePoints, 0, outOffset);
    };

    public static String isNullOrEmpty(String str) {
        if (StringUtils.isEmpty(str) || EMPTY_STR.equals(str)) {
            return "";
        }
        return str;
    }

    public static String isNull(String str) {
        if (StringUtils.isEmpty(str) || EMPTY_STR.equals(str)) {
            return "--";
        }
        return str;
    }

    public static String isNullForHtml(String str) {
        if (StringUtils.isEmpty(str) || EMPTY_STR.equals(str)) {
            return "<br>";
        }
        return str;
    }

    /**
     * Determine if it is an empty string
     *
     * @param cs string
     * @return whether it is empty
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Capitalize the first letter
     *
     * @param str string
     * @return capitalize the first letter of the result
     */
    public static String capitalize(final String str) {
        return FIRST_CHAR_HANDLER_FUN.apply(str, Character::toTitleCase);
    }

    /**
     * Lowercase first letter
     *
     * @param str string
     * @return the first letter of the result is lowercase
     */
    public static String uncapitalize(final String str) {
        return FIRST_CHAR_HANDLER_FUN.apply(str, Character::toLowerCase);
    }
}
