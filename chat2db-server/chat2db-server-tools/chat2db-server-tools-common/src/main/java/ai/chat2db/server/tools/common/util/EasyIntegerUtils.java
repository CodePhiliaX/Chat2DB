package ai.chat2db.server.tools.common.util;

public class EasyIntegerUtils {

    /**
     * Determine whether two Boolean values are the same
     *
     * @param b1
     * @param b2
     * @param defaultValue default value,
     *                     assuming that b1 b2 is empty,
     *                     which default value should be taken?
     * @return
     */
    public static boolean equals(Integer b1, Integer b2, Integer defaultValue) {
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
