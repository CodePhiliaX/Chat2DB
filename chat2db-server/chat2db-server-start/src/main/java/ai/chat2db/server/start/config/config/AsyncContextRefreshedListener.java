package ai.chat2db.server.start.config.config;

import ai.chat2db.server.tools.common.model.ConfigJson;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Execute tasks after startup is completed
 *
 * @author Jiaju Zhuang
 */
@Component
@Slf4j
public class AsyncContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    @Async
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Successfully set up startup
        String currentVersion = ConfigUtils.getLocalVersion();
        ConfigJson configJson = ConfigUtils.getConfig();
        if (StringUtils.isNotBlank(currentVersion) && !StringUtils.equals(currentVersion,
            configJson.getLatestStartupSuccessVersion())) {
            configJson.setLatestStartupSuccessVersion(currentVersion);
            ConfigUtils.setConfig(configJson);
        }
    }
}