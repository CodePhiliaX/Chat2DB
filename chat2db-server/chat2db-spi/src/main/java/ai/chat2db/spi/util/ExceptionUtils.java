package ai.chat2db.spi.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * exception utils
 */
@Slf4j
public class ExceptionUtils {

    /**
     * print stack trace
     *
     * @param throwable
     * @return
     */
    public static String getErrorInfoFromException(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            log.error("ErrorInfoFromException", e);
            return "ErrorInfoFromException";
        }
    }
}
