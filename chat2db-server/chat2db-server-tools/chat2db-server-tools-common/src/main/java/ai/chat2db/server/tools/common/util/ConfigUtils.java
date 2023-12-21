package ai.chat2db.server.tools.common.util;

import ai.chat2db.server.tools.common.model.ConfigJson;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Optional;

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
    public static String getLatestLocalVersion() {
        if (versionFile == null) {
            log.warn("VERSION_FILE is null");
            return null;
        }
        return StringUtils.trim(FileUtil.readUtf8String(versionFile));
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
//            log.info("user home: {}", System.getProperty("user.home"));
//            log.info("project.path: {}", System.getProperty("project.path"));
//            log.info("jarPath: {}", jarPath);
            return FileUtil.getParent(jarPath, 4);
        } catch (Exception e) {
            log.error("getAppPath error", e);
            return null;
        }
    }

    public static void pid() {
        try {
            log.error("pidinit");
            ProcessHandle currentProcess = ProcessHandle.current();
            long pid = currentProcess.pid();
            log.error("pid:{}", pid);
            String environment = StringUtils.defaultString(System.getProperty("spring.profiles.active"), "dev");
            File pidFile = new File(CONFIG_BASE_PATH + File.separator + "config" + File.separator + environment + "pid");
            if (!pidFile.exists()) {
                FileUtil.writeUtf8String(String.valueOf(pid), pidFile);
            } else {
                String oldPid = FileUtil.readUtf8String(pidFile);
                log.error("oldPid:{}", oldPid);
                if (StringUtils.isNotBlank(oldPid)) {
                    Optional<ProcessHandle> processHandle = ProcessHandle.of(Long.parseLong(oldPid));
                    log.error("processHandle:{}", JSON.toJSONString(processHandle));
                    processHandle.ifPresent(handle -> {
                        ProcessHandle.Info info = handle.info();
                        String[] arguments = info.arguments().orElse(null);
                        log.error("arguments:{}", JSON.toJSONString(arguments));
                        if (arguments == null) {
                            return;
                        }
                        for (String argument : arguments) {
                            if (StringUtils.equals("chat2db-server-start.jar", argument)) {
                                handle.destroy();
                                log.error("destroy old process--------");
                                break;
                            }
                            if (StringUtils.equals("application", argument)) {
                                handle.destroy();
                                log.error("destroy old process--------");
                                break;
                            }
                        }
                    });
                }

                FileUtil.writeUtf8String(String.valueOf(pid), pidFile);
            }

        }catch (Exception e){
            log.error("updatePid error",e);
        }

    }
}
