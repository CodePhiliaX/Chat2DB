/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package ai.chat2db.server.web.api.controller.system;

import ai.chat2db.server.domain.api.model.Config;
import ai.chat2db.server.domain.api.param.SystemConfigParam;
import ai.chat2db.server.domain.api.service.ConfigService;
import ai.chat2db.server.domain.core.cache.CacheManage;
import ai.chat2db.server.tools.base.wrapper.result.ActionResult;
import ai.chat2db.server.tools.base.wrapper.result.DataResult;
import ai.chat2db.server.tools.common.config.Chat2dbProperties;
import ai.chat2db.server.tools.common.enums.ModeEnum;
import ai.chat2db.server.tools.common.model.ConfigJson;
import ai.chat2db.server.tools.common.util.ConfigUtils;
import ai.chat2db.server.tools.common.util.EasyEnumUtils;
import ai.chat2db.server.web.api.controller.ai.chat2db.client.Chat2dbAIClient;
import ai.chat2db.server.web.api.controller.system.util.SystemUtils;
import ai.chat2db.server.web.api.controller.system.vo.AppVersionVO;
import ai.chat2db.server.web.api.controller.system.vo.SystemVO;
import ai.chat2db.server.web.api.util.ApplicationContextUtil;
import ai.chat2db.spi.ssh.SSHManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;


/**
 * @author jipengfei
 * @version : HomeController.java, v 0.1 2022年09月18日 14:52 jipengfei Exp $
 */
@RestController
@RequestMapping("/api/system")
@Slf4j
public class SystemController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Chat2dbProperties chat2dbProperties;

    @Autowired
    private ConfigService configService;

    /**
     * 检测是否成功
     *
     * @return
     */
    @GetMapping
    public DataResult<SystemVO> get() {
        ConfigJson configJson = ConfigUtils.getConfig();
        return DataResult.of(SystemVO.builder()
                .systemUuid(configJson.getSystemUuid())
                .build());
    }

    private static final String UPDATE_TYPE = "client_update_type";

    @GetMapping("/get_latest_version")
    public DataResult<AppVersionVO> getLatestVersion(String currentVersion) {
        String user = "";
        DataResult<Config> dataResult = configService.find(Chat2dbAIClient.CHAT2DB_OPENAI_KEY);
        if(dataResult.getData() != null){
            user = dataResult.getData().getContent();
        }
        AppVersionVO appVersionVO = SystemUtils.getLatestVersion(currentVersion, "manual", user);
        if(appVersionVO == null){
            appVersionVO = new AppVersionVO();
            appVersionVO.setVersion(currentVersion);
            appVersionVO.setType("manual");
        }
        DataResult<Config> updateType = configService.find(UPDATE_TYPE);
        if(updateType.getData() != null){
            appVersionVO.setType(updateType.getData().getContent());
        }

        ModeEnum mode = EasyEnumUtils.getEnum(ModeEnum.class, System.getProperty("chat2db.mode"));
        if (mode == ModeEnum.DESKTOP) {
            // In this mode, no user login is required, so only local access is available
            appVersionVO.setDesktop(true);
        }
        return DataResult.of(appVersionVO);
    }

    @PostMapping("/update_desktop_version")
    public DataResult<String> updateDesktopVersion(@RequestBody AppVersionVO version) {
        new Thread(() -> {
            SystemUtils.upgrade(version);
        }).start();
        return DataResult.of(version.getVersion());
    }

    @GetMapping("/is_update_success")
    public DataResult<Boolean> isUpdateSuccess(String version) {
        String localVersion = ConfigUtils.getLocalVersion();
        if (StringUtils.isEmpty(localVersion)) {
            return DataResult.of(false);
        }
        return DataResult.of(localVersion.equals(version));
    }
    @PostMapping("/set_update_type")
    public ActionResult setUpdateType(@RequestBody String updateType) {
        SystemConfigParam systemConfigParam = new SystemConfigParam();
        systemConfigParam.setCode(UPDATE_TYPE);
        systemConfigParam.setContent(updateType);
        systemConfigParam.setSummary("client update type");
        configService.createOrUpdate(systemConfigParam);
        return ActionResult.isSuccess();
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    @GetMapping("/get-version-a")
    public DataResult<String> getVersion() {
        return DataResult.of(chat2dbProperties.getVersion());
    }

    /**
     * 退出服务
     */
    @RequestMapping("/stop")
    public DataResult<String> stop() {
        log.info("退出应用");
        new Thread(() -> {
            // 会在100ms以后 退出后台
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("开始退出Spring应用");
            SSHManager.close();
            try {
                SpringApplication.exit(applicationContext);
            } catch (Exception ignore) {
            }
            // 有可能SpringApplication.exit 会退出失败
            // 直接系统退出
            log.info("开始退出系统应用");
            CacheManage.close();
            try {
                System.exit(0);
            } catch (Exception ignore) {
            }

        }).start();
        return DataResult.of("ok");
    }
}
