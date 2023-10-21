package ai.chat2db.server.web.api.controller.system;

import ai.chat2db.server.tools.common.util.ConfigUtils;
import ai.chat2db.server.web.api.controller.system.util.SystemUtils;
import ai.chat2db.server.web.api.controller.system.vo.AppVersionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AutomaticUpgrade {

    @Scheduled(fixedRate = 3600000) // 每小时运行一次
    public void checkVersionUpdates() {
        AppVersionVO appVersion = SystemUtils.getLatestVersion(ConfigUtils.getLocalVersion(), "auto", "");
        if (appVersion != null) {
            SystemUtils.upgrade(appVersion);
        }
    }
}
