package ai.chat2db.server.tools.common.util;

import ai.chat2db.server.tools.common.config.GlobalDict;
import ai.chat2db.server.tools.common.model.ConfigJson;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
    /**
     * 模板文件
     **/
    public static final List<String> TEMPLATE_FILE = Arrays.asList("template.html", "template_diy.docx", "sub_template_diy.docx");

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
        String templateDir = CONFIG_BASE_PATH + File.separator + "template";
        File file = new File(templateDir);
        if (!file.exists()) {
            file.mkdir();
        }
        for (String template : TEMPLATE_FILE) {
            saveFile(templateDir, template, true);
        }
    }

    public static void saveFile(String dir, String path, boolean isOverride) {
        if (!isOverride) {
            File file = new File(dir + File.separator + path);
            if (file.exists()) {
                return;
            }
        }
        try (// 模板文件输入输出地址 读取resources下文件
             FileOutputStream outputStream = new FileOutputStream(dir + File.separator + path);
             //返回读取指定资源的输入流
             InputStream inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream("template" + File.separator + path)) {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                outputStream.write(buffer, 0, n);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("getAppPath error", e);
        }
    }
}
