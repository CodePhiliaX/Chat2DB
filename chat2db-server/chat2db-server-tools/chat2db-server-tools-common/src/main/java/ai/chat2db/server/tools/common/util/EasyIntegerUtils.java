package ai.chat2db.server.tools.common.util;

public class EasyIntegerUtils {

    /**
     * 判断2个布尔值是否相同
     *
     * @param b1
     * @param b2
     * @param defaultValue 默认值 ，假设b1 b2为空的情况下 取哪个默认值
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
