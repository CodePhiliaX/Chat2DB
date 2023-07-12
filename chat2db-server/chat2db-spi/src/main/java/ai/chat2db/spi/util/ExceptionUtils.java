package ai.chat2db.spi.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * exception utils
 */
public class ExceptionUtils {

    /**
     * print stack trace
     *
     * @param throwable
     * @return
     */
    public static String getErrorInfoFromException(Throwable throwable) {
        String errorDetail = "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            errorDetail = " \r\n " + sw.toString() + " \r\n ";
            sw.close();
            pw.close();
        } catch (Exception e2) {
            return "ErrorInfoFromException";
        }
        return errorDetail;
    }
}
