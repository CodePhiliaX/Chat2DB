package ai.chat2db.server.tools.common.util;

import java.util.Objects;
import java.util.regex.Pattern;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.net.NetUtil;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;

/**
 * Log utility
 *
 * @author Jiaju Zhuang
 */
public class LogUtils {

    private static final ThreadLocal<String> TRACE_ID_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * Request headers for client IPs
     */
    private static final String[] CLIENT_IP_HEADERS = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP",
        "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    /**
     * Maximum log length
     */
    public static final int MAX_LOG_LENGTH = 20000;

    public static final String TRACE_ID = "TRACE_ID";
    public static final String TRACE_ID_HEADER = "X-Chat2DB-Trace-Id";

    /**
     * 换行符
     */
    private static final Pattern LINE_FEED_PATTERN = Pattern.compile("\r|\n");

    /**
     * 去除换行符
     *
     * @param log
     * @return
     */
    public static String removeCrlf(String log) {
        if (Objects.isNull(log)) {
            return null;
        }
        return LINE_FEED_PATTERN.matcher(log).replaceAll("");
    }

    /**
     * 裁切日志
     *
     * @param log
     * @return
     */
    public static String cutLog(Object log) {
        if (Objects.isNull(log)) {
            return null;
        }
        return EasyStringUtils.limitString(removeCrlf(log.toString()), MAX_LOG_LENGTH);
    }

    /**
     * 返回traceId
     *
     * @return
     */
    public static String generateTraceId() {
        String traceId = UUID.fastUUID().toString();
        TRACE_ID_THREAD_LOCAL.set(traceId);
        return traceId;
    }

    /**
     * Gets the trace ID
     *
     * @return
     */
    public static String getTraceId() {
        return TRACE_ID_THREAD_LOCAL.get();
    }

    /**
     * Remove the trace ID
     *
     * @return
     */
    public static void removeTraceId() {
        TRACE_ID_THREAD_LOCAL.remove();
    }

    /**
     * Obtain the client IP
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpRequest request) {
        HttpHeaders httpHeaders = request.getHeaders();
        String ip;
        for (String header : CLIENT_IP_HEADERS) {
            ip = httpHeaders.getFirst(header);
            if (!NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }
        ip = request.getRemote();
        return NetUtil.getMultistageReverseProxyIp(ip);
    }
}
