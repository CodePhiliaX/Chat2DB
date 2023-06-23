package ai.chat2db.server.tools.base.constant;

/**
 * 常量
 *
 * @author 是仪
 */
public interface EasyToolsConstant {

    /**
     * 日志的追踪id
     */
    String LOG_TRACE_ID = "EAGLEEYE_TRACE_ID";

    /**
     * 最大分页大小
     */
    int MAX_PAGE_SIZE = 1000;

    /**
     * 序列化id
     */
    long SERIAL_VERSION_UID = 1L;

    /**
     * 最大循环次数 防止很多循环进入死循环
     */
    int MAXIMUM_ITERATIONS = 10 * 1000;

}
