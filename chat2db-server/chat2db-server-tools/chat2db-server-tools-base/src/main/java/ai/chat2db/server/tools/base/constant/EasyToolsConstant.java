package ai.chat2db.server.tools.base.constant;

/**
 * constant
 *
 * @author Shi Yi
 */
public interface EasyToolsConstant {

    /**
     * Log tracking id
     */
    String LOG_TRACE_ID = "EAGLEEYE_TRACE_ID";

    /**
     * Maximum paging size
     */
    int MAX_PAGE_SIZE = 1000;

    /**
     * serializedid
     */
    long SERIAL_VERSION_UID = 1L;

    /**
     * Maximum number of loops to prevent many loops from entering an infinite loop
     */
    int MAXIMUM_ITERATIONS = 10 * 1000;

}
