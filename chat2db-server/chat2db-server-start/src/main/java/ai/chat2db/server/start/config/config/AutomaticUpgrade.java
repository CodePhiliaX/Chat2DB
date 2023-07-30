package ai.chat2db.server.start.config.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import ai.chat2db.server.tools.common.util.ConfigUtils;
import cn.hutool.core.io.FileUtil;
import com.dtflys.forest.Forest;
import com.dtflys.forest.utils.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AutomaticUpgrade {

    private static final OkHttpClient client = new OkHttpClient();

    private static final String VERSION_URL = "https://sqlgpt.cn/api/version.json";

    @Scheduled(fixedRate = 3600000) // 每小时运行一次
    public void checkVersionUpdates() {
        String appPath = ConfigUtils.APP_PATH;

        log.info("appPath: {}", appPath);
        if (StringUtils.isBlank(appPath) || !appPath.contains("app")) {
            return;
        }
        try {
            Upgrade dataResult = getUpgrade();

            String oldVersion = ConfigUtils.getLocalVersion();

            if (!needUpdate(dataResult, oldVersion)) {
                return;
            }

            File versionFile = new File(
                appPath + File.separator + "versions" + File.separator + dataResult.getVersion());
            if (!versionFile.exists()) {
                versionFile.mkdir();
            }
            File oldLib = new File(
                appPath + File.separator + "versions" + File.separator + oldVersion + File.separator + "static"
                    + File.separator + "lib");

            File newLib = new File(
                appPath + File.separator + "versions" + File.separator + dataResult.getVersion() + File.separator
                    + "static");

            if (oldLib.exists()) {
                FileUtil.copy(oldLib, newLib, true);
            }

            for (Map<String, String> downloadFile : dataResult.getDownloadFiles()) {
                downloadUpgrade(downloadFile, versionFile);
            }

            ConfigUtils.updateVersion(dataResult.getVersion());
        } catch (Exception e) {
            log.error("checkVersionUpdates error", e);
        }

    }

    private void downloadUpgrade(Map<String, String> downloadFile, File versionFile) throws IOException {
        String url = downloadFile.get("url");
        String fileName = downloadFile.get("fileName");
        String[] paths = fileName.split("/");
        String filePath = versionFile.getPath();
        for (int i = 0; i < paths.length; i++) {
            filePath = filePath + File.separator + paths[i];
            if (i < paths.length - 1) {
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdir();
                }
            }
        }
        download(url, filePath);
    }

    /**
     * Find upgrade files
     *
     * @return
     */
    private Upgrade getUpgrade() {
        return Forest.get(VERSION_URL)
            .connectTimeout(Duration.ofMillis(5000))
            .readTimeout(Duration.ofMillis(10000))
            .execute(new TypeReference<>() {});
    }

    private boolean needUpdate(Upgrade upgrade, String localVersion) {
        if (upgrade == null || StringUtils.isBlank(upgrade.getVersion()) || StringUtils.isBlank(localVersion)
            || upgrade.getVersion().equals(localVersion)) {
            return false;
        }
        String[] versionArray = upgrade.getVersion().split("\\.");
        String[] localVersionArray = localVersion.split("\\.");
        for (int i = 0; i < versionArray.length; i++) {
            if (Long.parseLong(versionArray[i]) > Long.parseLong(localVersionArray[i])) {
                return true;
            }
        }
        return false;
    }

    private void download(String url, String outputPath) throws IOException {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            try (InputStream is = response.body().byteStream(); FileOutputStream fos = new FileOutputStream(
                outputPath)) {

                byte[] buffer = new byte[2048];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
            }
            // System.out.println("File downloaded: " + outputPath);
        }
    }

    @Data
    public static class Upgrade implements Serializable {

        private String version;

        private List<Map<String, String>> downloadFiles;
    }
}
