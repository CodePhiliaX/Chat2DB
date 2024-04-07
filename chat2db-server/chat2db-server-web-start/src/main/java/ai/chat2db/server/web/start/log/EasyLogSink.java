package ai.chat2db.server.web.start.log;

import ai.chat2db.server.tools.common.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ContentTypeUtils;
import org.zalando.logbook.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
            log.error("Log exceptions", e);
        }
    }

    public void printLog(final Correlation correlation, final HttpRequest request, final HttpResponse response)
        throws IOException {
        // Encapsulate log object
        WebLog webLog = new WebLog();

        String method = request.getMethod();
        // path
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
            log.warn("The request to obtain the log & returns an exception. Most likely, the user has closed the stream.", e);
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
