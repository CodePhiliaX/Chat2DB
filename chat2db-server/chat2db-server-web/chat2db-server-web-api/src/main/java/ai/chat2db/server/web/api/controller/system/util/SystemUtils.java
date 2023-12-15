package ai.chat2db.server.web.api.controller.system.util;


import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import ai.chat2db.server.web.api.controller.system.vo.AppVersionVO;
import ai.chat2db.spi.ssh.SSHManager;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.utils.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.Duration;

/**
 * 系统工具包
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class SystemUtils {

    /**
     * 停止当前应用
     */
    public static void stop() {
        new Thread(() -> {
            log.info("1秒以后退出应用");
            // 1秒以后自动退出应用
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 直接系统退出
            log.info("开始退出系统应用");
            SSHManager.close();
            try {
                System.exit(0);
            } catch (Exception ignore) {
            }
        }).start();
    }

    private static final OkHttpClient client = new OkHttpClient();

    private static final String VERSION_URL = "https://sqlgpt.cn/api/version.json";

    private static final String ZIP_FILE_PATH = ConfigUtils.APP_PATH + File.separator + "versions" + File.separator;

    public static void upgrade(AppVersionVO appVersion) {

        String appPath = ConfigUtils.APP_PATH;

        log.info("appPath: {}", appPath);
        if (StringUtils.isBlank(appPath) || !appPath.contains("app")) {
            return;
        }
        try {
            String zipPath = ZIP_FILE_PATH + appVersion.getVersion() + ".zip";

            HttpUtil.downloadFile(appVersion.getHotUpgradeUrl(), ZIP_FILE_PATH + appVersion.getVersion() + ".zip");

            ZipUtil.unzip(zipPath);

            FileUtil.del(zipPath);

            ConfigUtils.updateVersion(appVersion.getVersion());
        } catch (Exception e) {
            log.error("checkVersionUpdates error", e);
        }
    }

    private static final String LATEST_VERSION_URL = "http://test.sqlgpt.cn/gateway/api/client/version/check/v3?version=%s&type=%s&userId=%s";

    public static AppVersionVO getLatestVersion(String version, String type, String userId) {
        String url = String.format(LATEST_VERSION_URL, version, type, userId);
        DataResult<AppVersionVO> result = Forest.get(url)
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(10000))
                .execute(new TypeReference<>() {
                });
        return result.getData();
    }

}
