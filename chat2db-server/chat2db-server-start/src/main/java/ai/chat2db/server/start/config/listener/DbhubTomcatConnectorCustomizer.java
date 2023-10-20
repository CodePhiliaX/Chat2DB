package ai.chat2db.server.start.config.listener;

import ai.chat2db.server.web.api.controller.system.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.stereotype.Component;

/**
 * 自定义tomcat参数
 *
 * @author Jiaju Zhuang
 */
@Component
@Slf4j
public class DbhubTomcatConnectorCustomizer implements TomcatConnectorCustomizer {
    @Override
    public void customize(Connector connector) {
        connector.addLifecycleListener(event -> {
            // 接受到关闭事件 直接退出系统，因为有时候不会退出系统
            if (LifecycleState.STOPPING == event.getLifecycle().getState()) {
                SystemUtils.stop();
            }
        });
    }
}
