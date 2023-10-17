package ai.chat2db.server.tools.common.util;

import ai.chat2db.server.tools.common.config.GlobalDict;
import ai.chat2db.server.tools.common.model.ConfigJson;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Configure information on the user side
 *
 * @author Jiaju Zhuang
 */
@Slf4j
public class ConfigUtils {
    public static String CONFIG_BASE_PATH = System.getProperty("user.home") + File.separator + ".chat2db";

    public static final String APP_PATH = getAppPath();

    private static String version = null;
    public static File versionFile = null;
    public static File configFile;
    private static ConfigJson config = null;

    static {
        String environment = StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
        if (APP_PATH != null) {
            versionFile = new File(
                    getAppPath() + File.separator + "versions" + File.separator + "version");
            if (!versionFile.exists()) {
                versionFile = null;
            }
        }
        configFile = new File(
                CONFIG_BASE_PATH + File.separator + "config" + File.separator + "config_" + environment + ".json");
        if (!configFile.exists()) {
            FileUtil.writeUtf8String(JSON.toJSONString(new ConfigJson()), configFile);
        }
        //复制模板
        copyTemplateFile();
    }

    public static void updateVersion(String version) {
        if (versionFile == null) {
            log.warn("VERSION_FILE is null");
            return;
        }
        FileUtil.writeUtf8String(version, versionFile);
        ConfigUtils.version = version;
    }

    public static String getLocalVersion() {
        if (versionFile == null) {
            log.warn("VERSION_FILE is null");
            return null;
        }
        if (version != null) {
            return version;
        }
        version = StringUtils.trim(FileUtil.readUtf8String(versionFile));
        return version;
    }

    public static ConfigJson getConfig() {
        if (config == null) {
            config = JSON.parseObject(StringUtils.trim(FileUtil.readUtf8String(configFile)), ConfigJson.class);
        }
        return config;
    }

    public static void setConfig(ConfigJson config) {
        String stringConfigJson = JSON.toJSONString(config);
        FileUtil.writeUtf8String(stringConfigJson, configFile);
        ConfigUtils.config = config;
        log.info("set config:{}", stringConfigJson);
    }

    private static String getAppPath() {
        try {
            String jarPath = System.getProperty("project.path");
            log.info("user home: {}", System.getProperty("user.home"));
            log.info("project.path: {}", System.getProperty("project.path"));
            log.info("jarPath: {}", jarPath);
            return FileUtil.getParent(jarPath, 4);
        } catch (Exception e) {
            log.error("getAppPath error", e);
            return null;
        }
    }

    public static void copyTemplateFile() {
        try {
            ClassPathResource resourceFolder = new ClassPathResource("template");
            // 复制文件夹到目标路径
            if (!getMD5(resourceFolder.getFile().getPath()).equals(getMD5(GlobalDict.templateDir))) {
                File targetFolder = new File(CONFIG_BASE_PATH);
                FileUtil.copy(resourceFolder.getFile(), targetFolder, true);
            }
        } catch (Exception e) {
            log.error("copy error", e);
        }
    }

    public static String getMD5(String folderPath) {
        File folder = new File(folderPath);
        List<File> files = FileUtil.loopFiles(folder);
        // 对文件列表进行排序
        Collections.sort(files);
        // 拼接文件内容
        StringBuilder stringBuilder = new StringBuilder();
        for (File file : files) {
            stringBuilder.append(FileUtil.readUtf8String(file));
        }
        // 计算 MD5 值
        return DigestUtils.md5DigestAsHex(stringBuilder.toString().getBytes());
    }
}
