package ai.chat2db.server.start.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

import ai.chat2db.server.tools.common.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ContentTypeUtils;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.Sink;

/**
 * log
 *
 * @author Jiaju Zhuang
 */
@Slf4j
@Component
public class EasyLogSink implements Sink {

    @Override
    public void write(final Precorrelation precorrelation, final HttpRequest request) {
    }

    @Override
    public void write(final Correlation correlation, final HttpRequest request, final HttpResponse response) {
        try {
            printLog(correlation, request, response);
        } catch (Exception e) {
            log.error("记录日志异常", e);
        }
    }

    public void printLog(final Correlation correlation, final HttpRequest request, final HttpResponse response)
        throws IOException {
        // 封装log 对象
        WebLog webLog = new WebLog();

        String method = request.getMethod();
        // 路径
        String path = request.getPath();

        webLog.setMethod(method);
        webLog.setPath(LogUtils.cutLog(path));
        webLog.setQuery(LogUtils.cutLog(request.getQuery()));
        webLog.setDuration(correlation.getDuration().toMillis());
        webLog.setStartTime(LocalDateTime.ofInstant(correlation.getStart(), ZoneId.systemDefault()));
        webLog.setEndTime(LocalDateTime.ofInstant(correlation.getEnd(), ZoneId.systemDefault()));
        try {
            webLog.setRequest(LogUtils.maskString(LogUtils.cutLog(new String(request.getBody(), StandardCharsets.UTF_8))));
            if (ContentTypeUtils.isContentTypeJSON(response.getContentType()) || ContentTypeUtils.isContentTypeHTML(
                response.getContentType())) {
                webLog.setResponse(LogUtils.maskString(LogUtils.cutLog(new String(response.getBody(), StandardCharsets.UTF_8))));
            } else {
                webLog.setResponse(response.getContentType() + ":[" + response.getBody().length + "]");
            }
        } catch (IOException e) {
            log.warn("获取日志的请求&返回异常，大概率是用户关闭了流。", e);
        }
        webLog.setIp(LogUtils.getClientIp(request));

        String pathAndQuery = path;
        if (StringUtils.isNotBlank(webLog.getQuery())) {
            pathAndQuery += "?" + webLog.getQuery();
        }
        log.info("http : {}|{}|{}|{}|{}", webLog.getMethod(), pathAndQuery, webLog.getDuration(),
            webLog.getRequest(), webLog.getResponse());
    }

}
