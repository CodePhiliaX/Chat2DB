package ai.chat2db.server.web.start.config.listener;

import ai.chat2db.server.web.api.controller.system.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Listener for application startup failure
 * The application failed to start. It just stopped tomcat and did not stop the application. Stop xia here.
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class FailedEventApplicationListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        log.error("Failed to start, stop application", event.getException());
        SystemUtils.stop();
    }
}