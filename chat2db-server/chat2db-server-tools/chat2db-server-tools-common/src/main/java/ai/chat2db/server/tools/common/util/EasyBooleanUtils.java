package ai.chat2db.server.tools.common.util;

/**
 * Boolean tool class
 *
 * @author Jiaju Zhuang
 */
public class EasyBooleanUtils {

    /**
     * Determine whether two Boolean values are the same
     *
     * @param b1
     * @param b2
     * @param defaultValue Default value, assuming that b1 and b2 are empty, which default value should be taken?
     * @return
     */
    public static boolean equals(Boolean b1, Boolean b2, Boolean defaultValue) {
        if (b1 == b2) {
            return true;
        }
        if (b1 == null) {
            b1 = defaultValue;
        }
        if (b2 == null) {
            b2 = defaultValue;
        }
        return b1 == b2;
    }

}
